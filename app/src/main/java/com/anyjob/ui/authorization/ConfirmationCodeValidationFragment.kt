package com.anyjob.ui.authorization

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.replace
import com.anyjob.R
import com.anyjob.databinding.FragmentConfirmationCodeValidationBinding
import com.anyjob.ui.authorization.viewModels.AuthorizationViewModel
import com.anyjob.ui.authorization.viewModels.ConfirmationCodeValidationViewModel
import com.anyjob.ui.explorer.ExplorerActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ConfirmationCodeValidationFragment : Fragment() {
    private val activityViewModel by sharedViewModel<AuthorizationViewModel>()
    private val viewModel by viewModel<ConfirmationCodeValidationViewModel>()

    private lateinit var binding: FragmentConfirmationCodeValidationBinding

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
        val description = "${getString(R.string.confirmation_code_fragment_description)} ${activityViewModel.phoneNumber.value}"
        binding.confirmationCodeValidationFragmentDescription.text = description
    }

    private fun useCodeValidatingStateObserver() {
        viewModel.isConfirmationCodeValid.observe(this@ConfirmationCodeValidationFragment) { isConfirmationCodeValid ->
            binding.confirmButton.isEnabled = true

            navigateToRegistrationFragment()
        }
    }

    private fun useValidateConfirmationCodeCommand() {
        binding.confirmButton.setOnClickListener {
            binding.confirmButton.isEnabled = false

            viewModel.validateConfirmationCode(
                binding.verificationCodeField.text.toString()
            )
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentConfirmationCodeValidationBinding.inflate(inflater, container, false)

        putPhoneNumberToDescription()
        useCodeValidatingStateObserver()

        useValidateConfirmationCodeCommand()

        return binding.root
    }
}