package com.anyjob.ui.authorization

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.replace
import com.anyjob.R
import com.anyjob.databinding.FragmentConfirmationCodeValidationBinding
import com.anyjob.ui.animations.VisibilityMode
import com.anyjob.ui.animations.extensions.slide
import com.anyjob.ui.animations.slide.SlideParameters
import com.anyjob.ui.authorization.viewModels.AuthorizationViewModel
import com.anyjob.ui.authorization.viewModels.ConfirmationCodeValidationViewModel
import com.anyjob.ui.explorer.ExplorerActivity
import com.anyjob.ui.extensions.afterTextChanged
import com.anyjob.ui.extensions.onEditorActionReceived
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ConfirmationCodeValidationFragment : Fragment() {
    private val _activityViewModel by sharedViewModel<AuthorizationViewModel>()
    private val _viewModel by viewModel<ConfirmationCodeValidationViewModel>()

    private lateinit var _binding: FragmentConfirmationCodeValidationBinding

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

    private fun navigateToRegistrationFragment() {
        val activity = requireActivity()
        val fragmentTransaction = activity.supportFragmentManager.beginTransaction()

        fragmentTransaction.replace<RegistrationFragment>(
            R.id.authorization_fragments_container
        )
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        .commit()
    }

    private fun navigateToExplorerActivity() {
        val activity = requireActivity()
        startActivity(
            Intent(context, ExplorerActivity::class.java)
        )

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

    private fun useOnCodeVerifiedObserver() {
        _activityViewModel.isCodeVerified.observe(this@ConfirmationCodeValidationFragment) { isCodeVerified ->
            if (isCodeVerified) {
                navigateToRegistrationFragment()
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentConfirmationCodeValidationBinding.inflate(inflater, container, false)

        putPhoneNumberToDescription()

        useCodeValidator()
        useOnCodeVerifiedObserver()

        useVerifyConfirmationCodeCommand()

        return _binding.root
    }
}