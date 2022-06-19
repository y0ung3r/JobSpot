package com.anyjob.ui.explorer

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.anyjob.R
import com.anyjob.databinding.ActivityExplorerBinding
import com.anyjob.domain.search.models.Order
import com.anyjob.ui.explorer.profile.models.AuthorizedUser
import com.anyjob.ui.explorer.search.controls.bottomSheets.AcceptJobBottomSheetDialog
import com.anyjob.ui.explorer.viewModels.ExplorerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

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

    private fun onWorkerAcceptOrder(order: Order) {
        _navigationController.navigate(R.id.path_to_job_overview_fragment_from_navigation_search)
    }

    private fun onClientFound(order: Order) {
        _viewModel.setOrder(order)
        _viewModel.setClient(order)

        val geocoder = Geocoder(
            this@ExplorerActivity,
            Locale.getDefault()
        )

        lifecycleScope.launch {
            val address = withContext(Dispatchers.Default) {
                geocoder.getFromLocation(order.address.latitude, order.address.longitude, 1)[0]
            }

            val street = address.thoroughfare
            val houseNumber = address.subThoroughfare

            AcceptJobBottomSheetDialog(
                _viewModel,
                order,
                "$street, $houseNumber",
                ::onClientFound,
                ::onWorkerAcceptOrder,
                this@ExplorerActivity,
                R.style.Theme_AnyJob_BottomSheetDialog,
            )
            .show()
        }
    }

    private fun onUserReady(user: AuthorizedUser?) {
        if (user != null) {
            val locale = Locale.getDefault()
            val fullnameField = binding.drawerLayout.findViewById<TextView>(R.id.fullname_field)
            val ratingField = binding.drawerLayout.findViewById<TextView>(R.id.user_rating)
            val phoneNumberField =
                binding.drawerLayout.findViewById<TextView>(R.id.phone_number_field)

            fullnameField.text = user.fullname
            ratingField.text = "%.1f".format(user.averageRate)
            phoneNumberField.text = PhoneNumberUtils.formatNumber(
                user.phoneNumber,
                locale.country
            )

            if (user.currentOrder != null && user.currentOrder.invokerId == user.id) {
                return _navigationController.navigate(R.id.path_to_order_overview_fragment_from_navigation_search)
            }
            else if (user.currentOrder != null && user.currentOrder.executorId == user.id) {
                return _navigationController.navigate(R.id.path_to_job_overview_fragment_from_navigation_search)
            }

            if (user.isWorker) {
                _viewModel.startClientSearching {
                    onClientFound(it)
                }
            }
        }
    }

    private fun drawAddressInToolbar(address: Address) {
        val street = address.thoroughfare
        val houseNumber = address.subThoroughfare
        val isAddressExists = street != null && street.isNotBlank() && houseNumber != null && houseNumber.isNotBlank()

        if (isAddressExists) {
            binding.toolbar.title = "$street, $houseNumber"
            binding.toolbar.subtitle = getString(R.string.address_title)
        }
        else {
            binding.toolbar.title = getString(R.string.failed_to_determine_address)
            binding.toolbar.subtitle = null
        }
    }

    private fun onAddressChanged(address: Address) {
        drawAddressInToolbar(address)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener(::onDrawerOpenButtonClick)

        _viewModel.currentAddress.observe(this@ExplorerActivity, ::onAddressChanged)
        _viewModel.getAuthorizedUser().observe(this@ExplorerActivity, ::onUserReady)

        /*val navigationItems = setOf(
            R.id.navigation_home,
            R.id.navigation_dashboard,
            R.id.navigation_profile
        )

        val applicationBarConfiguration = AppBarConfiguration(navigationItems)
        setupActionBarWithNavController(_navigationController, applicationBarConfiguration)
        binding.navigationView.setupWithNavController(_navigationController)*/
    }

    override fun onBackPressed() {
        // Ignore...
    }
}