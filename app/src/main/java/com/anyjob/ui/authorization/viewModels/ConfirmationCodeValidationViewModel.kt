package com.anyjob.ui.authorization.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConfirmationCodeValidationViewModel : ViewModel() {
    private val _isConfirmationCodeValid = MutableLiveData<Boolean>()
    val isConfirmationCodeValid: LiveData<Boolean> = _isConfirmationCodeValid

    private fun isCodeValid(code: String): Boolean {
        return code.isNotBlank()
    }

    fun validateCode(code: String) {
        _isConfirmationCodeValid.value = isCodeValid(code = code)
    }
}