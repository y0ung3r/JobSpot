package com.anyjob.ui.authorization.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthorizationViewModel : ViewModel() {
    private val _isConfirmationCodeSent = MutableLiveData<Boolean>()
    val isConfirmationCodeSent: LiveData<Boolean> = _isConfirmationCodeSent

    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String> = _phoneNumber

    fun sendConfirmationCode(phoneNumber: String) {
        _phoneNumber.value = phoneNumber
        _isConfirmationCodeSent.value = true
    }
}