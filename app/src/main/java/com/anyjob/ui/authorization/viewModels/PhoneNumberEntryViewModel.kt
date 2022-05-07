package com.anyjob.ui.authorization.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PhoneNumberEntryViewModel() : ViewModel() {
    private val _isConfirmationCodeSent = MutableLiveData<Boolean>()
    val isConfirmationCodeSent: LiveData<Boolean> = _isConfirmationCodeSent

    private val _isPhoneNumberValid = MutableLiveData<Boolean>()
    val isPhoneNumberValid: LiveData<Boolean> = _isPhoneNumberValid

    fun sendConfirmationCode(phoneNumber: String) {
        _isConfirmationCodeSent.value = true
    }

    private fun isPhoneNumberValid(phoneNumber: String): Boolean {
        return phoneNumber.isNotBlank()
    }

    fun validatePhoneNumber(phoneNumber: String) {
        _isPhoneNumberValid.value = isPhoneNumberValid(phoneNumber = phoneNumber)
    }
}