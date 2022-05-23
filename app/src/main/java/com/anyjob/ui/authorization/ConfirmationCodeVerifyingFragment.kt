package com.anyjob.ui.authorization

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anyjob.R
import com.anyjob.data.authorization.FirebasePhoneNumberAuthorizationParameters
import com.anyjob.databinding.FragmentConfirmationCodeVerifyingBinding
import com.anyjob.domain.authorization.exceptions.AuthorizationCanceledException
import com.anyjob.domain.authorization.exceptions.AuthorizationServerException
import com.anyjob.domain.authorization.exceptions.InvalidCredentialsException
import com.anyjob.ui.animations.VisibilityMode
import com.anyjob.ui.animations.extensions.slide
import com.anyjob.ui.authorization.viewModels.AuthorizationViewModel
import com.anyjob.ui.authorization.viewModels.ConfirmationCodeVerifyingViewModel
import com.anyjob.ui.extensions.afterTextChanged
import com.anyjob.ui.extensions.getMaxLength
import com.anyjob.ui.extensions.onEditorActionReceived
import com.anyjob.ui.extensions.showToast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ConfirmationCodeVerifyingFragment : Fragment() {
    private val _activityViewModel by sharedViewModel<AuthorizationViewModel>()
    private val _viewModel by viewModel<ConfirmationCodeVerifyingViewModel>()

    private lateinit var _binding: FragmentConfirmationCodeVerifyingBinding
    private val _navigationController by lazy {
        findNavController()
    }

    private fun appendPhoneNumberToDescription() {
        val description = "${getString(R.string.confirmation_code_fragment_description)} ${_activityViewModel.phoneNumber.value}"
        _binding.confirmationCodeValidationFragmentDescription.text = description
    }

    private fun navigateToExplorerActivity() {
        _navigationController.navigate(R.id.path_to_explorer_activity_from_confirmation_code_validation_fragment)

        val activity = requireActivity()
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }

    private fun navigateToRegisterFragment() {
        _navigationController.navigate(R.id.path_to_navigation_registration_fragment_action)
    }

    private fun verifyCode(code: String) {
        val codeIsValid = _viewModel.isConfirmationCodeValid.value

        if (codeIsValid != null && codeIsValid) {
            _binding.loadingBar.slide(VisibilityMode.Show)

            _binding.verificationCodeField.isEnabled = false
            _binding.verifyButton.isEnabled = false

            _activityViewModel.verifyCode(code)
        }
    }

    private fun onCodeValidating(isValid: Boolean) {
        _binding.verifyButton.isEnabled = isValid

        if (!isValid) {
            _binding.verificationCodeField.error = getString(R.string.invalid_confirmation_code_format)
        }
    }

    private fun onCodeChanged(code: String) {
        _viewModel.validateCode(code)

        val maxCodeLength = _binding.verificationCodeField.getMaxLength()

        if (code.length == maxCodeLength) {
            verifyCode(code)
        }
    }

    private fun onCodeVerified(result: Result<Unit>) {
        result.onSuccess {
            navigateToRegisterFragment()
        }
        .onFailure { exception ->
            val errorMessage = getString(
                when (exception) {
                    is InvalidCredentialsException -> R.string.incorrect_confirmation_code
                    is IllegalArgumentException -> R.string.invalid_confirmation_code_format
                    is AuthorizationCanceledException -> R.string.authorization_canceled_error
                    else -> R.string.confirmation_code_verification_error
                }
            )

            showToast(errorMessage)
        }

        _binding.loadingBar.slide(VisibilityMode.Hide)
        _binding.verificationCodeField.isEnabled = true
        _binding.verifyButton.isEnabled = true
    }

    private fun onCodeResent(result: Result<Unit>) {
        result.onSuccess {
            _binding.resendButton.isEnabled = false

            showToast("${getString(R.string.confirmation_code_resent_successfully)} ${_activityViewModel.phoneNumber.value}")

            val resendTimeout = _activityViewModel.resendTimeout.value!!.times(1000L)
            val cooldownTimer = object : CountDownTimer(resendTimeout, 1000L) {
                override fun onTick(milliseconds: Long) {
                    val seconds = milliseconds.div(1000L)
                    _binding.resendButton.text = "${getString(R.string.resend_code_action)} (${seconds + 1})"
                }

                override fun onFinish() {
                    _binding.resendButton.isEnabled = true
                    _binding.resendButton.text = getString(R.string.resend_code_action)
                }
            }

            cooldownTimer.start()
        }
        .onFailure { exception ->
            val errorMessage = getString(
                when (exception) {
                    is InvalidCredentialsException -> R.string.invalid_phone_number_format
                    is AuthorizationServerException -> R.string.authorization_server_error
                    else -> R.string.confirmation_code_send_failed
                }
            )

            showToast(errorMessage)
        }

        _binding.loadingBar.slide(VisibilityMode.Hide)
        _binding.verificationCodeField.isEnabled = true
        _binding.verifyButton.isEnabled = true
    }

    private fun onConfirmButtonClick(button: View) {
        val code = _binding.verificationCodeField.text.toString()
        verifyCode(code)
    }

    private fun onResendButtonClick(button: View) {
        val phoneNumber = _activityViewModel.phoneNumber.value

        if (phoneNumber != null) {
            _binding.loadingBar.slide(VisibilityMode.Show)

            _binding.verificationCodeField.isEnabled = false
            _binding.verifyButton.isEnabled = false

            _activityViewModel.resendVerificationCode()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentConfirmationCodeVerifyingBinding.inflate(inflater, container, false)

        appendPhoneNumberToDescription()

        _viewModel.isConfirmationCodeValid.observe(this@ConfirmationCodeVerifyingFragment, ::onCodeValidating)
        _activityViewModel.onCodeVerified.observe(this@ConfirmationCodeVerifyingFragment, ::onCodeVerified)
        _activityViewModel.onCodeResent.observe(this@ConfirmationCodeVerifyingFragment, ::onCodeResent)

        _binding.verifyButton.setOnClickListener(::onConfirmButtonClick)
        _binding.resendButton.setOnClickListener(::onResendButtonClick)

        _binding.verificationCodeField.apply {
            onEditorActionReceived(EditorInfo.IME_ACTION_SEND, ::verifyCode)
            afterTextChanged(::onCodeChanged)
        }

        return _binding.root
    }
}