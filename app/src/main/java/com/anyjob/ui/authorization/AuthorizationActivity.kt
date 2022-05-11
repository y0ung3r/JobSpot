package com.anyjob.ui.authorization

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.add
import com.anyjob.databinding.ActivityAuthorizationBinding
import com.anyjob.ui.authorization.viewModels.AuthorizationViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthorizationActivity : AppCompatActivity() {
    private val _viewModel by viewModel<AuthorizationViewModel>()

    private val _binding: ActivityAuthorizationBinding by lazy {
        ActivityAuthorizationBinding.inflate(layoutInflater)
    }

    private fun navigateToPhoneNumberEntryFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.add<PhoneNumberEntryFragment>(
            _binding.authorizationFragmentsContainer.id
        )
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        .commit()
    }

    private fun useErrorsHandler() {
        _viewModel.errorMessageCode.observe(this@AuthorizationActivity) { errorMessageCode ->
            val errorMessage = getString(errorMessageCode)
            Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_LONG)
                 .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)
        supportActionBar?.hide()

        useErrorsHandler()

        navigateToPhoneNumberEntryFragment()
    }
}