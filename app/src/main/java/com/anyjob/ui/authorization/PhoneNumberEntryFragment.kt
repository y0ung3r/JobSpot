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
import com.anyjob.databinding.FragmentPhoneNumberEntryBinding
import com.anyjob.ui.animations.VisibilityMode
import com.anyjob.ui.animations.extensions.fade
import com.anyjob.ui.animations.fade.FadeParameters
import com.anyjob.ui.authorization.viewModels.AuthorizationViewModel
import com.anyjob.ui.authorization.viewModels.PhoneNumberEntryViewModel
import com.anyjob.ui.extensions.afterTextChanged
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhoneNumberEntryFragment : Fragment() {
    private val activityViewModel by sharedViewModel<AuthorizationViewModel>()
    private val viewModel by viewModel<PhoneNumberEntryViewModel>()
    private lateinit var binding: FragmentPhoneNumberEntryBinding

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
        binding.phoneNumberField.afterTextChanged {
            phoneNumber -> viewModel.validatePhoneNumber(phoneNumber)
        }

        viewModel.isPhoneNumberValid.observe(this@PhoneNumberEntryFragment) { isPhoneNumberValid ->
            binding.sendConfirmationCodeButton.isEnabled = isPhoneNumberValid

            if (!isPhoneNumberValid) {
                binding.phoneNumberField.error = getString(R.string.invalid_phone_number)
            }
        }
    }

    private fun useCodeSendingStateObserver() {
        activityViewModel.isConfirmationCodeSent.observe(this@PhoneNumberEntryFragment) { isConfirmationCodeSent ->
            binding.loadingBar.fade(
                FadeParameters().apply {
                    mode = VisibilityMode.Hide
                    animationLength = 300
                }
            )

            binding.phoneNumberField.isEnabled = true
            binding.sendConfirmationCodeButton.isEnabled = true

            if (isConfirmationCodeSent)  {
                navigateToConfirmationCodeValidationFragment()
            }
            else {
                Toast.makeText(context, R.string.confirmation_code_send_failed, Toast.LENGTH_LONG)
                     .show()
            }
        }
    }

    private fun useSendConfirmationCodeCommand() {
        binding.sendConfirmationCodeButton.setOnClickListener {
            binding.loadingBar.fade(
                FadeParameters().apply {
                    mode = VisibilityMode.Hide
                    animationLength = 300
                }
            )

            binding.phoneNumberField.isEnabled = false
            binding.sendConfirmationCodeButton.isEnabled = false

            activityViewModel.sendConfirmationCode(
                binding.phoneNumberField.text.toString()
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPhoneNumberEntryBinding.inflate(inflater, container, false)

        usePhoneNumberValidator()
        useCodeSendingStateObserver()

        useSendConfirmationCodeCommand()

        return binding.root
    }
}