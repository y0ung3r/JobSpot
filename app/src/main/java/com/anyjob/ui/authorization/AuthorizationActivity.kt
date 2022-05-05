package com.anyjob.ui.authorization

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.anyjob.R
import com.anyjob.databinding.ActivityAuthorizationBinding
import com.anyjob.ui.animations.AnimationParameters
import com.anyjob.ui.animations.extensions.fadeIn
import com.anyjob.ui.animations.extensions.fadeOut
import com.anyjob.ui.animations.fade.FadeOutParameters
import com.anyjob.ui.authorization.viewModels.AuthorizationViewModel
import com.anyjob.ui.extensions.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthorizationActivity : AppCompatActivity() {

    private val authorizationViewModel by viewModel<AuthorizationViewModel>()

    private val binding: ActivityAuthorizationBinding by lazy {
        ActivityAuthorizationBinding.inflate(layoutInflater)
    }

    private fun usePhoneNumberValidator() {
        binding.phoneNumberField.afterTextChanged {
            phoneNumber -> authorizationViewModel.validatePhoneNumber(phoneNumber)
        }
    }

    private fun useErrorIfPhoneNumberNotValid() {
        authorizationViewModel.isPhoneNumberValid.observe(this@AuthorizationActivity) { isPhoneNumberValid ->
            binding.sendConfirmationCodeButton.isEnabled = isPhoneNumberValid

            if (!isPhoneNumberValid) {
                binding.phoneNumberField.error = getString(R.string.invalid_phone_number)
            }
        }
    }

    private fun useErrorIfConfirmationCodeNotSent() {
        authorizationViewModel.isConfirmationCodeSent.observe(this@AuthorizationActivity) { isConfirmationCodeSent ->
            binding.loadingBar.fadeOut(
                FadeOutParameters().apply {
                    goneAfterAnimation = true
                    animationLength = 1500
                }
            )

            binding.phoneNumberField.isEnabled = true
            binding.sendConfirmationCodeButton.isEnabled = true

            if (!isConfirmationCodeSent) {
                Toast.makeText(baseContext, R.string.confirmation_code_send_failed, Toast.LENGTH_LONG)
                     .show()
            }
            else {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private val sendConfirmationCode = View.OnClickListener {
        binding.loadingBar.fadeIn(
            AnimationParameters().apply {
                animationLength = 1500
            }
        )

        binding.phoneNumberField.isEnabled = false
        binding.sendConfirmationCodeButton.isEnabled = false

        authorizationViewModel.sendConfirmationCode(
            binding.phoneNumberField.text.toString()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        usePhoneNumberValidator()
        useErrorIfPhoneNumberNotValid()

        useErrorIfConfirmationCodeNotSent()
        binding.sendConfirmationCodeButton.setOnClickListener(sendConfirmationCode)
    }
}