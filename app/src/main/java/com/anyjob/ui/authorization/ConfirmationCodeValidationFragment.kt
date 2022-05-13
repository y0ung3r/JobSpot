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
import com.anyjob.data.authorization.firebase.FirebasePhoneNumberAuthorizationParameters
import com.anyjob.databinding.FragmentConfirmationCodeValidationBinding
import com.anyjob.ui.animations.VisibilityMode
import com.anyjob.ui.animations.extensions.slide
import com.anyjob.ui.animations.slide.SlideParameters
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

    private fun setBusy(isBusy: Boolean) {
        val visibilityMode = when (isBusy) {
            true -> VisibilityMode.Show
            false -> VisibilityMode.Hide
        }

        _binding.verificationCodeField.isEnabled = !isBusy
        _binding.confirmButton.isEnabled = !isBusy

        _binding.loadingBar.slide(
            SlideParameters().apply {
                mode = visibilityMode
                animationLength = 300
            }
        )
    }

    private fun navigateToExplorerActivity() {
        _navigationController.navigate(R.id.path_to_explorer_activity_from_confirmation_code_validation_fragment)

        val activity = requireActivity()
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }

    private fun putPhoneNumberToDescription() {
        val description = "${getString(R.string.confirmation_code_fragment_description)} ${_activityViewModel.phoneNumber.value}"
        _binding.confirmationCodeValidationFragmentDescription.text = description
    }

    private fun useCodeValidator() {
        _binding.verificationCodeField.apply {
            afterTextChanged {
                code -> _viewModel.validateCode(code)
            }

            onEditorActionReceived(EditorInfo.IME_ACTION_SEND) { code ->
                val codeIsValid = _viewModel.isConfirmationCodeValid.value

                if (codeIsValid != null && codeIsValid) {
                    setBusy(true)
                    _activityViewModel.verifyCode(code)
                }
            }
        }

        _viewModel.isConfirmationCodeValid.observe(this@ConfirmationCodeValidationFragment) { isConfirmationCodeValid ->
            _binding.confirmButton.isEnabled = isConfirmationCodeValid

            if (!isConfirmationCodeValid) {
                _binding.verificationCodeField.error = getString(R.string.invalid_confirmation_code_format)
            }
        }
    }

    private fun useOnCodeResentObserver() {
        _activityViewModel.isCodeResent.observe(this@ConfirmationCodeValidationFragment) { isCodeResent ->
            setBusy(false)

            if (isCodeResent) {
                val message = "${getString(R.string.confirmation_code_resent_successfully)} ${_activityViewModel.phoneNumber.value}"
                Toast.makeText(context, message, Toast.LENGTH_LONG)
                     .show()

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

        _activityViewModel.sentErrorMessageCode.observe(this@ConfirmationCodeValidationFragment) { errorMessageCode ->
            setBusy(false)

            val errorMessage = getString(errorMessageCode)
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG)
                 .show()
        }
    }

    private fun useOnCodeVerifiedObserver() {
        _activityViewModel.isCodeVerified.observe(this@ConfirmationCodeValidationFragment) { isCodeVerified ->
            if (isCodeVerified) {
                _navigationController.navigate(R.id.path_to_navigation_registration_fragment_action)
            }
        }

        _activityViewModel.verificationErrorMessageCode.observe(this@ConfirmationCodeValidationFragment) { errorMessageCode ->
            setBusy(false)

            val errorMessage = getString(errorMessageCode)
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG)
                 .show()
        }
    }

    private fun useVerifyConfirmationCodeCommand() {
        _binding.confirmButton.setOnClickListener {
            setBusy(true)

            val code = _binding.verificationCodeField.text.toString()
            _activityViewModel.verifyCode(code)
        }
    }

    private fun useResendConfirmationCodeCommand() {
        _binding.resendButton.setOnClickListener {
            setBusy(true)
            _binding.resendButton.isEnabled = false

            val phoneNumber = _activityViewModel.phoneNumber.value

            if (phoneNumber != null) {
                val authorizationParameters = FirebasePhoneNumberAuthorizationParameters(
                    phoneNumber = phoneNumber,
                    activity = requireActivity()
                )

                _activityViewModel.sendVerificationCode(authorizationParameters)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentConfirmationCodeValidationBinding.inflate(inflater, container, false)

        putPhoneNumberToDescription()

        useCodeValidator()
        useOnCodeVerifiedObserver()
        useOnCodeResentObserver()

        useVerifyConfirmationCodeCommand()
        useResendConfirmationCodeCommand()

        return _binding.root
    }
}