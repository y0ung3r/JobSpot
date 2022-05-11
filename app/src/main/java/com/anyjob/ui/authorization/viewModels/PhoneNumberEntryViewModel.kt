package com.anyjob.ui.authorization.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PhoneNumberEntryViewModel : ViewModel() {
    private val _isPhoneNumberValid = MutableLiveData<Boolean>()
    val isPhoneNumberValid: LiveData<Boolean> = _isPhoneNumberValid

    fun setPhoneNumberMaskFilled(isFilled: Boolean) {
        _isPhoneNumberValid.value = isFilled
    }
}