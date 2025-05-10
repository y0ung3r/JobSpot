package com.jobspot.ui.authorization

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.jobspot.R
import com.jobspot.databinding.ActivityAuthorizationBinding
import com.jobspot.ui.authorization.viewModels.AuthorizationViewModel
import com.jobspot.ui.extensions.observeOnce
import com.yandex.mapkit.MapKitFactory
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthorizationActivity : AppCompatActivity() {
    private val _viewModel by viewModel<AuthorizationViewModel>()

    private val _binding: ActivityAuthorizationBinding by lazy {
        ActivityAuthorizationBinding.inflate(layoutInflater)
    }

    private val _navigationController: NavController by lazy {
        val navigationHost = supportFragmentManager.findFragmentById(_binding.authorizationFragmentsContainer.id) as NavHostFragment
        navigationHost.navController
    }

    private fun navigateToExplorerActivity() {
        _navigationController.navigate(R.id.path_to_explorer_activity_from_authorization_activity)

        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun navigateToProfileCreationFragment() {
        _navigationController.navigate(R.id.path_to_profile_creation_fragment_from_authorization_activity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(_binding.root)

        _binding.authorizationFragmentsContainer.visibility = View.INVISIBLE

        _viewModel.getAuthorizedUser().observeOnce(this@AuthorizationActivity) { authorizedUser ->
            if (authorizedUser != null && authorizedUser.fullname.isNotBlank()) {
                return@observeOnce navigateToExplorerActivity()
            }

            if (authorizedUser != null) {
                navigateToProfileCreationFragment()
            }

            _binding.authorizationFragmentsContainer.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}