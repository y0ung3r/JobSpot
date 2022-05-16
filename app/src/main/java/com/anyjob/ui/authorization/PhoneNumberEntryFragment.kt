package com.anyjob.ui.authorization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anyjob.R
import com.anyjob.domain.authorization.exceptions.AuthorizationServerException
import com.anyjob.domain.authorization.exceptions.InvalidCredentialsException
import com.anyjob.data.authorization.FirebasePhoneNumberAuthorizationParameters
import com.anyjob.databinding.FragmentPhoneNumberEntryBinding
import com.anyjob.ui.animations.VisibilityMode
import com.anyjob.ui.animations.extensions.slide
import com.anyjob.ui.authorization.viewModels.AuthorizationViewModel
import com.anyjob.ui.authorization.viewModels.PhoneNumberEntryViewModel
import com.anyjob.ui.extensions.onEditorActionReceived
import com.anyjob.ui.extensions.setupMask
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhoneNumberEntryFragment : Fragment() {
    private val _activityViewModel by sharedViewModel<AuthorizationViewModel>()
    private val _viewModel by viewModel<PhoneNumberEntryViewModel>()

    private lateinit var _binding: FragmentPhoneNumberEntryBinding
    private val _navigationController by lazy {
        findNavController()
    }

    private fun sendCode(phoneNumber: String) {
        if (_binding.sendConfirmationCodeButton.isEnabled) {
            _binding.loadingBar.slide(VisibilityMode.Show)
            _binding.phoneNumberField.isEnabled = false
            _binding.sendConfirmationCodeButton.isEnabled = false

            val authorizationParameters = FirebasePhoneNumberAuthorizationParameters(
                phoneNumber = phoneNumber,
                activity = requireActivity()
            )

            _activityViewModel.sendVerificationCode(authorizationParameters)
        }
    }

    private fun onPhoneNumberChanged(maskFilled: Boolean, extractedValue: String, formattedValue: String) {
        _binding.sendConfirmationCodeButton.isEnabled = maskFilled

        if (!maskFilled) {
            _binding.phoneNumberField.error = getString(R.string.invalid_phone_number_format)
        }
    }

    private fun onCodeSent(result: Result<Boolean>) {
        result.onSuccess {
            _navigationController.navigate(R.id.path_to_confirmation_code_verifying_fragment_action)
        }
        .onFailure { exception ->
            val errorMessage = getString(
                when (exception) {
                    is InvalidCredentialsException -> R.string.invalid_phone_number_format
                    is AuthorizationServerException -> R.string.authorization_server_error
                    else -> R.string.confirmation_code_send_failed
                }
            )

            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG)
                 .show()
        }

        _binding.loadingBar.slide(VisibilityMode.Hide)
        _binding.phoneNumberField.isEnabled = true
        _binding.sendConfirmationCodeButton.isEnabled = true
    }

    private fun onSendButtonClick(button: View) {
        val phoneNumber = _binding.phoneNumberField.text.toString()
        sendCode(phoneNumber)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPhoneNumberEntryBinding.inflate(inflater, container, false)

        _activityViewModel.onCodeSent.observe(this@PhoneNumberEntryFragment, ::onCodeSent)

        _binding.sendConfirmationCodeButton.setOnClickListener(::onSendButtonClick)

        _binding.phoneNumberField.setupMask("+7 ([000]) [000]-[00]-[00]", ::onPhoneNumberChanged)
        _binding.phoneNumberField.onEditorActionReceived(EditorInfo.IME_ACTION_SEND, ::sendCode)

        return _binding.root
    }
}