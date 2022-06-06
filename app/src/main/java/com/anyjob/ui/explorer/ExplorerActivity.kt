package com.anyjob.ui.explorer

import android.location.Address
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.anyjob.R
import com.anyjob.databinding.ActivityExplorerBinding
import com.anyjob.ui.explorer.viewModels.ExplorerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExplorerActivity : AppCompatActivity() {
    private val _viewModel by viewModel<ExplorerViewModel>()
    private val _binding: ActivityExplorerBinding by lazy {
        ActivityExplorerBinding.inflate(layoutInflater)
    }

    private val _navigationController: NavController by lazy {
        val navigationHost = supportFragmentManager.findFragmentById(_binding.explorerFragmentsContainer.id) as NavHostFragment
        navigationHost.navController
    }

    private fun onDrawerOpenButtonClick(view: View) {
        _binding.drawerLayout.open()
    }

    private fun onAddressChanged(address: Address) {
        val street = address.thoroughfare
        val houseNumber = address.subThoroughfare
        val isAddressExists = street != null && street.isNotBlank() && houseNumber != null && houseNumber.isNotBlank()

        if (isAddressExists) {
            _binding.toolbar.subtitle = "$street, $houseNumber"
        }
        else {
            _binding.toolbar.subtitle = getString(R.string.address_not_exists_error)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)

        _binding.toolbar.setNavigationOnClickListener(::onDrawerOpenButtonClick)

        _viewModel.currentAddress.observe(this@ExplorerActivity, ::onAddressChanged)

        /*val navigationItems = setOf(
            R.id.navigation_home,
            R.id.navigation_dashboard,
            R.id.navigation_profile
        )

        val applicationBarConfiguration = AppBarConfiguration(navigationItems)
        setupActionBarWithNavController(_navigationController, applicationBarConfiguration)
        _binding.navigationView.setupWithNavController(_navigationController)*/
    }
}