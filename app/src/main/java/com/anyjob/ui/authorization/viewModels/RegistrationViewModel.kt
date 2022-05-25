package com.anyjob.ui.authorization.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegistrationViewModel : ViewModel() {
    private val _isLastnameFilled = MutableLiveData<Boolean>()
    val isLastnameFilled: LiveData<Boolean> = _isLastnameFilled

    private val _isFirstnameFilled = MutableLiveData<Boolean>()
    val isFirstnameFilled: LiveData<Boolean> = _isFirstnameFilled

    private val _isDataValid = MutableLiveData<Boolean>()
    val isFieldsValidated: LiveData<Boolean> = _isDataValid

    fun validateLastname(lastname: String) {
        _isLastnameFilled.postValue(
            lastname.isNotBlank()
        )
    }

    fun validateFirstname(firstname: String) {
        _isFirstnameFilled.postValue(
            firstname.isNotBlank()
        )
    }
}