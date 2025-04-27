package com.anyjob.ui.explorer

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.anyjob.R
import com.anyjob.data.search.GeolocationUpdater
import com.anyjob.databinding.ActivityExplorerBinding
import com.anyjob.domain.search.models.Order
import com.anyjob.ui.explorer.profile.models.AuthorizedUser
import com.anyjob.ui.explorer.search.controls.bottomSheets.AcceptJobBottomSheetDialog
import com.anyjob.ui.explorer.viewModels.ExplorerViewModel
import com.yandex.mapkit.GeoObject
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
import java.util.*
import java.util.concurrent.TimeUnit

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

                AcceptJobBottomSheetDialog(
                    _viewModel,
                    order,
                    "${geoObject.name}",
                    ::onClientFound,
                    ::onWorkerAcceptOrder,
                    this@ExplorerActivity,
                    R.style.Theme_AnyJob_BottomSheetDialog,
                )
                .show()
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

    private fun onUserReady(user: AuthorizedUser?) {
        if (user == null)
            return

        val locale = Locale.getDefault()
        val fullnameField = binding.drawerLayout.findViewById<TextView>(R.id.fullname_field)
        val ratingField = binding.drawerLayout.findViewById<TextView>(R.id.user_rating)
        val phoneNumberField = binding.drawerLayout.findViewById<TextView>(R.id.phone_number_field)

        val logoutButton = binding.drawerLayout.findViewById<TextView>(R.id.logout_button_title)
        logoutButton.setOnClickListener {
            onLogoutButtonClick()
        }

        fullnameField.text = user.fullname
        ratingField.text = "%.1f".format(user.averageRate)
        phoneNumberField.text = PhoneNumberUtils.formatNumber(user.phoneNumber, locale.country)

        val workManager = WorkManager.getInstance(applicationContext)

        if (user.isWorker) {
            _viewModel.startClientSearching {
                onClientFound(it)
            }

            val geolocationUpdateRequest = OneTimeWorkRequestBuilder<GeolocationUpdater>()
                .setInputData(workDataOf("USER_ID" to user.id))
                .setInitialDelay(5, TimeUnit.SECONDS)
                .build()

            workManager
                .enqueueUniqueWork(
                    GeolocationUpdater::class.java.name,
                    ExistingWorkPolicy.KEEP,
                    geolocationUpdateRequest)
        }
        else {
            workManager.cancelUniqueWork(GeolocationUpdater::class.java.name)
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

    override fun onBackPressed() {
        // Ignore...
    }
}