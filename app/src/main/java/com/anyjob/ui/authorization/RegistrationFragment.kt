package com.anyjob.ui.authorization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anyjob.R
import com.anyjob.databinding.FragmentRegistrationBinding
import com.anyjob.ui.authorization.viewModels.RegistrationViewModel
import com.anyjob.ui.extensions.afterTextChanged
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegistrationFragment : Fragment() {
    private val _viewModel by viewModel<RegistrationViewModel>()
    private lateinit var _binding: FragmentRegistrationBinding

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)

        _viewModel.isLastnameFilled.observe(this@RegistrationFragment, ::onLastnameValidating)
        _viewModel.isFirstnameFilled.observe(this@RegistrationFragment, ::onFirstnameValidating)

        _binding.lastnameField.afterTextChanged(::onLastnameChanged)
        _binding.firstnameField.afterTextChanged(::onFirstnameChanged)

        return _binding.root
    }
}