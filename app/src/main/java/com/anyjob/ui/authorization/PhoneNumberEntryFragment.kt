package com.anyjob.ui.authorization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.replace
import com.anyjob.R
import com.anyjob.data.authorization.firebase.FirebasePhoneNumberAuthorizationParameters
import com.anyjob.databinding.FragmentPhoneNumberEntryBinding
import com.anyjob.ui.animations.VisibilityMode
import com.anyjob.ui.animations.extensions.fade
import com.anyjob.ui.animations.fade.FadeParameters
import com.anyjob.ui.authorization.viewModels.AuthorizationViewModel
import com.anyjob.ui.authorization.viewModels.PhoneNumberEntryViewModel
import com.anyjob.ui.extensions.afterTextChanged
import com.google.firebase.auth.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhoneNumberEntryFragment : Fragment() {
    private val _activityViewModel by sharedViewModel<AuthorizationViewModel>()
    private val _viewModel by viewModel<PhoneNumberEntryViewModel>()
    private lateinit var _binding: FragmentPhoneNumberEntryBinding

    private fun isBusy(isBusy: Boolean) {
        _binding.loadingBar.fade(
            FadeParameters().apply {
                mode = if (isBusy) VisibilityMode.Show else VisibilityMode.Hide
                animationLength = 300
            }
        )

        _binding.phoneNumberField.isEnabled = !isBusy
        _binding.sendConfirmationCodeButton.isEnabled = !isBusy
    }

    private fun navigateToConfirmationCodeValidationFragment() {
        val activity = requireActivity()
        val fragmentTransaction = activity.supportFragmentManager.beginTransaction()

        fragmentTransaction.replace<ConfirmationCodeValidationFragment>(
            R.id.authorization_fragments_container
        )
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        .commit()
    }

    private fun usePhoneNumberValidator() {
        _binding.phoneNumberField.afterTextChanged {
            phoneNumber -> _viewModel.validatePhoneNumber(phoneNumber)
        }

        _viewModel.isPhoneNumberValid.observe(this@PhoneNumberEntryFragment) { isPhoneNumberValid ->
            _binding.sendConfirmationCodeButton.isEnabled = isPhoneNumberValid

            if (!isPhoneNumberValid) {
                _binding.phoneNumberField.error = getString(R.string.invalid_phone_number)
            }
        }
    }

    private fun useCodeSendingStateObserver() {
        _activityViewModel.isConfirmationCodeSent.observe(this@PhoneNumberEntryFragment) { isConfirmationCodeSent ->
            if (isConfirmationCodeSent) {
                navigateToConfirmationCodeValidationFragment()
            }
        }

        _activityViewModel.errorMessageCode.observe(this@PhoneNumberEntryFragment) { errorMessageCode ->
            isBusy(false)

            val errorMessage = getString(errorMessageCode)
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG)
                 .show()
        }
    }

    private fun useSendConfirmationCodeCommand() {
        _binding.sendConfirmationCodeButton.setOnClickListener {
            isBusy(true)

            val phoneNumber = _binding.phoneNumberField.text.toString()
            val authorizationParameters = FirebasePhoneNumberAuthorizationParameters(
                phoneNumber = phoneNumber,
                activity = requireActivity()
            )

            _activityViewModel.sendVerificationCode(authorizationParameters)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPhoneNumberEntryBinding.inflate(inflater, container, false)

        usePhoneNumberValidator()
        useCodeSendingStateObserver()

        useSendConfirmationCodeCommand()

        return _binding.root
    }
}