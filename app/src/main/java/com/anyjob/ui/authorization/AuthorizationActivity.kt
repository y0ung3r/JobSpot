package com.anyjob.ui.authorization

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.anyjob.R
import com.anyjob.databinding.ActivityAuthorizationBinding
import com.anyjob.ui.animations.AnimationParameters
import com.anyjob.ui.animations.extensions.fadeIn
import com.anyjob.ui.animations.extensions.fadeOut
import com.anyjob.ui.animations.fade.FadeOutParameters
import com.anyjob.ui.authorization.viewModels.AuthorizationViewModel
import com.anyjob.ui.authorization.viewModels.AuthorizationViewModelFactory
import com.anyjob.ui.extensions.*

class AuthorizationActivity : AppCompatActivity() {

    private val authorizationViewModel: AuthorizationViewModel by lazy {
        val factory = AuthorizationViewModelFactory()
        ViewModelProvider(this@AuthorizationActivity, factory)[AuthorizationViewModel::class.java]
    }

    private val binding: ActivityAuthorizationBinding by lazy {
        ActivityAuthorizationBinding.inflate(layoutInflater)
    }

    private fun toWaitingState() {
        binding.loadingBar.fadeIn(
            AnimationParameters().apply {
                animationLength = 1500
            }
        )

        binding.phoneNumberField.isEnabled = false
        binding.getConfirmationCodeButton.isEnabled = false
    }

    private fun toIdleState() {
        binding.loadingBar.fadeOut(
            FadeOutParameters().apply {
                goneAfterAnimation = true
                animationLength = 1500
            }
        )

        binding.phoneNumberField.isEnabled = true
        binding.getConfirmationCodeButton.isEnabled = true
    }

    private fun observeActivityErrors() {
        authorizationViewModel.loginFormState.observe(this@AuthorizationActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            binding.getConfirmationCodeButton.isEnabled = loginState.isDataValid

            if (loginState.phoneNumberError != null) {
                binding.phoneNumberField.error = getString(loginState.phoneNumberError)
            }
        })
    }

    private fun observeAuthorizationResults() {
        authorizationViewModel.loginResult.observe(this@AuthorizationActivity, Observer {
            val loginResult = it ?: return@Observer

            toIdleState()

            if (loginResult.error != null) {
                Toast.makeText(baseContext, R.string.login_failed, Toast.LENGTH_SHORT)
                     .show()
            }

            setResult(Activity.RESULT_OK)
            finish()
        })
    }

    private val getConfirmationCode = View.OnClickListener {
        toWaitingState()

        val phoneNumber = binding.phoneNumberField.text.toString()
        authorizationViewModel.login(phoneNumber)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        observeActivityErrors()
        observeAuthorizationResults()

        binding.phoneNumberField.afterTextChanged {
            phoneNumber -> authorizationViewModel.validateLoginForm(phoneNumber)
        }

        binding.getConfirmationCodeButton.setOnClickListener(getConfirmationCode)
    }
}