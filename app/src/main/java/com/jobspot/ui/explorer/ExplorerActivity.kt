package com.jobspot.ui.explorer

import android.Manifest
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.work.WorkManager
import com.jobspot.R
import com.jobspot.data.search.DefaultGeolocationUpdater
import com.jobspot.databinding.ActivityExplorerBinding
import com.jobspot.domain.search.models.Order
import com.jobspot.ui.animations.VisibilityMode
import com.jobspot.ui.animations.extensions.slide
import com.jobspot.ui.animations.slide.SlideFrom
import com.jobspot.ui.animations.slide.SlideParameters
import com.jobspot.ui.explorer.profile.models.AuthorizedUser
import com.jobspot.ui.explorer.search.controls.bottomSheets.AcceptJobBottomSheetDialog
import com.jobspot.ui.explorer.viewModels.ExplorerViewModel
import com.jobspot.ui.extensions.observeOnce
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.runtime.Error
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class ExplorerActivity : AppCompatActivity() {
    private val _viewModel by viewModel<ExplorerViewModel>()

    val binding: ActivityExplorerBinding by lazy {
        ActivityExplorerBinding.inflate(layoutInflater)
    }

    private val _navigationController: NavController by lazy {
        val navigationHost = supportFragmentManager.findFragmentById(binding.explorerFragmentsContainer.id) as NavHostFragment
        navigationHost.navController
    }

    private fun onDrawerOpenButtonClick(view: View) {
        binding.drawerLayout.open()
    }

    fun onLogoutButtonClick() {
        _viewModel.logout()
        _navigationController.navigate(R.id.path_to_authorization_activity_from_explorer_activity)
    }

    private fun onWorkerAcceptOrder(order: Order) {
        _navigationController.navigate(R.id.path_to_job_overview_fragment_from_navigation_search)
    }

    private fun onClientFound(order: Order) {
        _viewModel.setOrder(order)
        _viewModel.setClient(order)

        val searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)

        val searchListener = object : Session.SearchListener {
            override fun onSearchResponse(response: Response) {
                val geoObject = response.collection.children.firstNotNullOf { it.obj }

                _viewModel.client.observeOnce(this@ExplorerActivity) {
                    AcceptJobBottomSheetDialog(
                        _viewModel,
                        it,
                        order,
                        "${geoObject.name}",
                        ::onClientFound,
                        ::onWorkerAcceptOrder,
                        this@ExplorerActivity,
                        R.style.Theme_JobSpot_BottomSheetDialog,
                    )
                    .show()
                }
            }

            override fun onSearchError(error: Error) {
                // showToast(getString(R.string.failed_to_determine_address))
            }
        }

        searchManager.submit(
            Point(order.address.latitude, order.address.longitude),
            16,
            SearchOptions().apply {
                searchTypes = SearchType.GEO.value
                resultPageSize = 1
            },
            searchListener
        )
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun onUserReady(user: AuthorizedUser?) {
        if (user == null)
            return

        val logoutButton = binding.drawerLayout.findViewById<TextView>(R.id.logout_button_title)
        logoutButton.setOnClickListener {
            onLogoutButtonClick()
        }

        if (user.isWorker) {
            _viewModel.startVerificationListener {
                binding.verificationBar.slide(SlideParameters().apply {
                    mode = VisibilityMode.Hide
                    from = SlideFrom.Top
                })
            }

            if (!user.isDocumentsVerified)
                binding.verificationBar.slide(SlideParameters().apply {
                    mode = VisibilityMode.Show
                    from = SlideFrom.Top
                })

            _viewModel.startClientSearching {
                onClientFound(it)
            }

            _viewModel.createGeolocationUpdater(applicationContext)
                .start(user.id)
        }

        if (user.currentOrder == null)
            return

        if (user.currentOrder.invokerId == user.id && user.currentOrder.executorId != null)
            return _navigationController.navigate(R.id.path_to_order_overview_fragment_from_navigation_search)

        if (user.currentOrder.executorId == user.id)
            return _navigationController.navigate(R.id.path_to_job_overview_fragment_from_navigation_search)
    }

    private fun drawAddressInToolbar(geoObject: GeoObject) {
        val toponym = geoObject.metadataContainer.getItem(ToponymObjectMetadata::class.java)
        val isAddressExists = toponym?.balloonPoint != null

        if (isAddressExists) {
            binding.toolbar.title = geoObject.name
            binding.toolbar.subtitle = getString(R.string.address_title)
        }
        else {
            binding.toolbar.title = getString(R.string.failed_to_determine_address)
            binding.toolbar.subtitle = null
        }
    }

    private fun onAddressChanged(geoObject: GeoObject) {
        drawAddressInToolbar(geoObject)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener(::onDrawerOpenButtonClick)

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                // Nothing
            }

            override fun onDrawerOpened(drawerView: View) {
                val locale = Locale.getDefault()
                val fullnameField = binding.drawerLayout.findViewById<TextView>(R.id.fullname_field)
                val ratingField = binding.drawerLayout.findViewById<TextView>(R.id.user_rating)
                val phoneNumberField = binding.drawerLayout.findViewById<TextView>(R.id.phone_number_field)

                _viewModel.getAuthorizedUser().observeOnce(this@ExplorerActivity) {
                    if (it == null)
                        return@observeOnce

                    fullnameField.text = it.fullname
                    ratingField.text = "%.1f".format(it.averageRate)
                    phoneNumberField.text = PhoneNumberUtils.formatNumber(it.phoneNumber, locale.country)
                }
            }

            override fun onDrawerClosed(drawerView: View) {
                // Nothing
            }

            override fun onDrawerStateChanged(newState: Int) {
                // Nothing
            }

        })

        reloadObservers()

        /*val navigationItems = setOf(
            R.id.navigation_home,
            R.id.navigation_dashboard,
            R.id.navigation_profile
        )

        val applicationBarConfiguration = AppBarConfiguration(navigationItems)
        setupActionBarWithNavController(_navigationController, applicationBarConfiguration)
        binding.navigationView.setupWithNavController(_navigationController)*/
    }

    fun reloadObservers() {
        _viewModel.currentGeoObject.removeObservers(this@ExplorerActivity)
        _viewModel.getAuthorizedUser().removeObservers(this@ExplorerActivity)
        _viewModel.currentGeoObject.observe(this@ExplorerActivity, ::onAddressChanged)
        _viewModel.getAuthorizedUser().observe(this@ExplorerActivity, ::onUserReady)
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isOpen)
            binding.drawerLayout.close()
    }
}