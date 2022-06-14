package com.anyjob.ui.authorization

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anyjob.R
import com.anyjob.databinding.FragmentProfileCreationBinding
import com.anyjob.domain.authorization.ProfileCreationParameters
import com.anyjob.ui.authorization.viewModels.AuthorizationViewModel
import com.anyjob.ui.authorization.viewModels.ProfileCreationViewModel
import com.anyjob.ui.explorer.search.controls.bottomSheets.addresses.AddressesBottomSheetDialog
import com.anyjob.ui.explorer.search.controls.bottomSheets.addresses.models.UserAddress
import com.anyjob.ui.extensions.afterTextChanged
import com.anyjob.ui.extensions.observeOnce
import com.anyjob.ui.extensions.showToast
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileCreationFragment : Fragment() {
    private val _activityViewModel by sharedViewModel<AuthorizationViewModel>()
    private val _viewModel by viewModel<ProfileCreationViewModel>()
    private lateinit var _addressesBottomSheet: AddressesBottomSheetDialog

    private lateinit var _binding: FragmentProfileCreationBinding
    private val _navigationController by lazy {
        findNavController()
    }

    private fun navigateToExplorerActivity() {
        _navigationController.navigate(R.id.path_to_explorer_activity_from_registration_fragment)

        val activity = requireActivity()
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }

    private fun isFieldValid(field: TextInputEditText): Boolean {
        return field.error == null || (field.error != null && field.error.isBlank())
    }

    private fun updateConfirmButton() {
        _binding.confirmButton.isEnabled = isFieldValid(_binding.lastnameField) && isFieldValid(_binding.firstnameField)
    }

    private fun onLastnameValidating(isValid: Boolean) {
        if (!isValid) {
            _binding.lastnameField.error = getString(R.string.invalid_lastname)
        }
    }

    private fun onFirstnameValidating(isValid: Boolean) {
        if (!isValid) {
            _binding.firstnameField.error = getString(R.string.invalid_firstname)
        }
    }

    private fun onLastnameChanged(lastname: String) {
        _viewModel.validateLastname(lastname)
        updateConfirmButton()
    }

    private fun onFirstnameChanged(firstname: String) {
        _viewModel.validateFirstname(firstname)
        updateConfirmButton()
    }

    private fun onProfileCreated(result: Result<Unit>) {
        result.onSuccess {
            navigateToExplorerActivity()
        }
        .onFailure {
            val errorMessage = getString(R.string.unexpected_error)
            showToast(errorMessage)

            _binding.confirmButton.isEnabled = true
            _binding.lastnameField.isEnabled = true
            _binding.firstnameField.isEnabled = true
            _binding.middlenameField.isEnabled = true
            _binding.isWorkerCheckBox.isEnabled = true
        }
    }

    private fun onConfirmButtonClick(button: View) {
        _binding.confirmButton.isEnabled = false
        _binding.lastnameField.isEnabled = false
        _binding.firstnameField.isEnabled = false
        _binding.middlenameField.isEnabled = false
        _binding.isWorkerCheckBox.isEnabled = false

        _activityViewModel.getAuthorizedUser().observeOnce(this@ProfileCreationFragment) { authorizedUser ->
            authorizedUser?.let {
                val profileCreationParameters = ProfileCreationParameters(
                    userId = it.id,
                    lastname = _binding.lastnameField.text.toString(),
                    firstname = _binding.firstnameField.text.toString(),
                    middlename = _binding.middlenameField.text.toString(),
                    isWorker = _binding.isWorkerCheckBox.isChecked
                )

                _viewModel.createProfile(profileCreationParameters)
            }
        }
    }

    private fun onHomeAddressChanged(address: UserAddress) {
        _binding.selectHomeAddressButton.text = address.formattedAddress
        _addressesBottomSheet.dismiss()
    }

    private fun onSelectHomeAddressButtonClick(button: View) {
        _addressesBottomSheet = AddressesBottomSheetDialog(
            requireContext(),
            R.style.Theme_AnyJob_BottomSheetDialog,
            ::onHomeAddressChanged
        )

        _addressesBottomSheet.show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileCreationBinding.inflate(inflater, container, false)

        _viewModel.isLastnameFilled.observe(this@ProfileCreationFragment, ::onLastnameValidating)
        _viewModel.isFirstnameFilled.observe(this@ProfileCreationFragment, ::onFirstnameValidating)
        _viewModel.onProfileCreated.observe(this@ProfileCreationFragment, ::onProfileCreated)

        _binding.lastnameField.afterTextChanged(::onLastnameChanged)
        _binding.firstnameField.afterTextChanged(::onFirstnameChanged)
        _binding.selectHomeAddressButton.setOnClickListener(::onSelectHomeAddressButtonClick)
        _binding.confirmButton.setOnClickListener(::onConfirmButtonClick)

        return _binding.root
    }
}