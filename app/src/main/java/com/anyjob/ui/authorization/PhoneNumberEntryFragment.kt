package com.anyjob.ui.authorization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.replace
import com.anyjob.R
import com.anyjob.data.authorization.firebase.FirebasePhoneNumberAuthorizationParameters
import com.anyjob.databinding.FragmentPhoneNumberEntryBinding
import com.anyjob.ui.animations.VisibilityMode
import com.anyjob.ui.animations.extensions.slide
import com.anyjob.ui.animations.slide.SlideParameters
import com.anyjob.ui.authorization.viewModels.AuthorizationViewModel
import com.anyjob.ui.authorization.viewModels.PhoneNumberEntryViewModel
import com.anyjob.ui.extensions.afterTextChanged
import com.anyjob.ui.extensions.attachMaskedTextChangedListener
import com.anyjob.ui.extensions.onTextChanged
import com.google.firebase.auth.*
import com.redmadrobot.inputmask.MaskedTextChangedListener
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhoneNumberEntryFragment : Fragment() {
    private val _activityViewModel by sharedViewModel<AuthorizationViewModel>()
    private val _viewModel by viewModel<PhoneNumberEntryViewModel>()

    private lateinit var _binding: FragmentPhoneNumberEntryBinding

    private fun setBusy(isBusy: Boolean) {
        val visibilityMode = when (isBusy) {
            true -> VisibilityMode.Show
            false -> VisibilityMode.Hide
        }

        _binding.sendConfirmationCodeButton.isEnabled = !isBusy
        _binding.phoneNumberField.isEnabled = !isBusy

        _binding.loadingBar.slide(
            SlideParameters().apply {
                mode = visibilityMode
                animationLength = 300
            }
        )
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
        val maskedTextChangedListener = MaskedTextChangedListener(
            "+7 ([000]) [000]-[00]-[00]",
            _binding.phoneNumberField
        )

        maskedTextChangedListener.onTextChanged { maskFilled, _, _ ->
            _viewModel.setPhoneNumberMaskFilled(maskFilled)
        }

        _binding.phoneNumberField.apply {
            attachMaskedTextChangedListener(maskedTextChangedListener)

            setOnEditorActionListener { phoneNumberField, actionId, _ ->
                val phoneNumber = phoneNumberField.text.toString()

                when (actionId) {
                    EditorInfo.IME_ACTION_SEND -> {
                        val phoneNumberIsValid = _viewModel.isPhoneNumberValid.value

                        if (phoneNumberIsValid != null && phoneNumberIsValid) {
                            sendConfirmationCode(phoneNumber)
                        }
                    }
                }
                false
            }
        }

        _viewModel.isPhoneNumberValid.observe(this@PhoneNumberEntryFragment) { isPhoneNumberValid ->
            _binding.sendConfirmationCodeButton.isEnabled = isPhoneNumberValid

            if (!isPhoneNumberValid) {
                _binding.phoneNumberField.error = getString(R.string.invalid_phone_number_format)
            }
        }
    }

    private fun useOnCodeSentObserver() {
        _activityViewModel.isCodeSent.observe(this@PhoneNumberEntryFragment) { isCodeSent ->
            if (isCodeSent) {
                navigateToConfirmationCodeValidationFragment()
            }
        }

        _activityViewModel.errorMessageCode.observe(this@PhoneNumberEntryFragment) {
            setBusy(false)
        }
    }

    private fun sendConfirmationCode(phoneNumber: String) {
        setBusy(true)

        val authorizationParameters = FirebasePhoneNumberAuthorizationParameters(
            phoneNumber = phoneNumber,
            activity = requireActivity()
        )

        _activityViewModel.sendVerificationCode(authorizationParameters)
    }

    private fun useSendConfirmationCodeCommand() {
        _binding.sendConfirmationCodeButton.setOnClickListener {
            val phoneNumber = _binding.phoneNumberField.text.toString()
            sendConfirmationCode(phoneNumber)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPhoneNumberEntryBinding.inflate(inflater, container, false)

        usePhoneNumberValidator()
        useOnCodeSentObserver()

        useSendConfirmationCodeCommand()

        return _binding.root
    }
}