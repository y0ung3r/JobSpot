package com.anyjob.ui.authorization.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anyjob.data.LoginRepository
import com.anyjob.data.Result

import com.anyjob.R
import com.anyjob.ui.authorization.dto.LoggedInUser
import com.anyjob.ui.authorization.AuthorizationActivityState
import com.anyjob.ui.authorization.dto.AuthorizationResult

class AuthorizationViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<AuthorizationActivityState>()
    val loginFormState: LiveData<AuthorizationActivityState> = _loginForm

    private val _loginResult = MutableLiveData<AuthorizationResult>()
    val loginResult: LiveData<AuthorizationResult> = _loginResult

    fun login(phoneNumber: String) {
        val result = loginRepository.login(phoneNumber)

        _loginResult.value = if (result is Result.Success)
            AuthorizationResult(success = LoggedInUser(displayName = result.data.displayName))
        else
            AuthorizationResult(error = R.string.login_failed)
    }

    fun validateLoginForm(phoneNumber: String) {
        _loginForm.value = if (isPhoneNumberValid(phoneNumber))
            AuthorizationActivityState(isDataValid = true)
        else
            AuthorizationActivityState(phoneNumberError = R.string.invalid_phone_number)
    }

    private fun isPhoneNumberValid(phoneNumber: String): Boolean {
        return phoneNumber.isNotBlank()
    }

    // A placeholder username validation check
    /*private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }*/

    // A placeholder password validation check
    /*private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }*/
}