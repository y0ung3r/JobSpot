package com.anyjob.ui.explorer

import android.location.Address
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.anyjob.R
import com.anyjob.databinding.ActivityExplorerBinding
import com.anyjob.ui.explorer.profile.models.AuthorizedUser
import com.anyjob.ui.explorer.viewModels.ExplorerViewModel
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

    private fun onUserReady(user: AuthorizedUser?) {
        if (user != null) {
            val locale = Locale.getDefault()
            val fullnameField = binding.drawerLayout.findViewById<TextView>(R.id.fullname_field)
            val phoneNumberField = binding.drawerLayout.findViewById<TextView>(R.id.phone_number_field)

            fullnameField.text = user.fullname
            phoneNumberField.text = PhoneNumberUtils.formatNumber(
                user.phoneNumber,
                locale.country
            )
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