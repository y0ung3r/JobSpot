package com.anyjob.ui.authorization.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConfirmationCodeValidationViewModel : ViewModel() {
    private val _isConfirmationCodeValid = MutableLiveData<Boolean>()
    val isConfirmationCodeValid: LiveData<Boolean> = _isConfirmationCodeValid

    private val _isUserRegistered = MutableLiveData<Boolean>()
    val isUserRegistered: LiveData<Boolean> = _isUserRegistered

    fun validateConfirmationCode(code: String) {
        _isConfirmationCodeValid.value = true
        _isUserRegistered.value = false
    }
}