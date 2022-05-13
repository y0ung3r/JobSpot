package com.anyjob.ui.authorization

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anyjob.R
import com.anyjob.data.authorization.exceptions.AuthorizationServerException
import com.anyjob.data.authorization.exceptions.InvalidCredentialsException
import com.anyjob.data.authorization.firebase.FirebasePhoneNumberAuthorizationParameters
import com.anyjob.databinding.FragmentConfirmationCodeValidationBinding
import com.anyjob.ui.animations.VisibilityMode
import com.anyjob.ui.animations.extensions.slide
import com.anyjob.ui.authorization.viewModels.AuthorizationViewModel
import com.anyjob.ui.authorization.viewModels.ConfirmationCodeValidationViewModel
import com.anyjob.ui.extensions.afterTextChanged
import com.anyjob.ui.extensions.onEditorActionReceived
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ConfirmationCodeValidationFragment : Fragment() {
    private val _activityViewModel by sharedViewModel<AuthorizationViewModel>()
    private val _viewModel by viewModel<ConfirmationCodeValidationViewModel>()

    private lateinit var _binding: FragmentConfirmationCodeValidationBinding
    private val _navigationController by lazy {
        findNavController()
    }

    private fun showToast(message: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(context, message, duration).show()
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
            _binding.confirmButton.isEnabled = false

            _activityViewModel.verifyCode(code)
        }
    }

    private fun onCodeValidating(isValid: Boolean) {
        _binding.confirmButton.isEnabled = isValid

        if (!isValid) {
            _binding.verificationCodeField.error = getString(R.string.invalid_confirmation_code_format)
        }
    }

    private fun onCodeChanged(code: String) {
        _viewModel.validateCode(code)
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
                    else -> R.string.confirmation_code_verification_error
                }
            )

            showToast(errorMessage)
        }

        _binding.loadingBar.slide(VisibilityMode.Hide)

        _binding.verificationCodeField.isEnabled = true
        _binding.confirmButton.isEnabled = true
    }

    private fun onCodeResent(result: Result<Boolean>) {
        result.onSuccess { isCodeResent ->
            if (isCodeResent) {
                _binding.resendButton.isEnabled = false

                showToast("${getString(R.string.confirmation_code_resent_successfully)} ${_activityViewModel.phoneNumber.value}")

                val cooldownTimer = object : CountDownTimer(60000L, 1000L) {
                    override fun onTick(milliseconds: Long) {
                        val seconds = milliseconds / 1000
                        _binding.resendButton.text = "${getString(R.string.resend_code_action)} (${seconds})"
                    }

                    override fun onFinish() {
                        _binding.resendButton.isEnabled = true
                        _binding.resendButton.text = getString(R.string.resend_code_action)
                    }
                }

                cooldownTimer.start()
            }
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

            _binding.verificationCodeField.isEnabled = true
            _binding.confirmButton.isEnabled = true
        }

        _binding.loadingBar.slide(VisibilityMode.Hide)
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
            _binding.confirmButton.isEnabled = false

            val authorizationParameters = FirebasePhoneNumberAuthorizationParameters(
                phoneNumber = phoneNumber,
                activity = requireActivity()
            )

            _activityViewModel.sendVerificationCode(authorizationParameters)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentConfirmationCodeValidationBinding.inflate(inflater, container, false)

        appendPhoneNumberToDescription()

        _viewModel.isConfirmationCodeValid.observe(this@ConfirmationCodeValidationFragment, ::onCodeValidating)
        _activityViewModel.onCodeVerified.observe(this@ConfirmationCodeValidationFragment, ::onCodeVerified)
        _activityViewModel.onCodeSent.observe(this@ConfirmationCodeValidationFragment, ::onCodeResent)

        _binding.confirmButton.setOnClickListener(::onConfirmButtonClick)
        _binding.resendButton.setOnClickListener(::onResendButtonClick)

        _binding.verificationCodeField.apply {
            onEditorActionReceived(EditorInfo.IME_ACTION_SEND, ::verifyCode)
            afterTextChanged(::onCodeChanged)
        }

        return _binding.root
    }
}