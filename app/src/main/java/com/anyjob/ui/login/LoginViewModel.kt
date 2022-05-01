package com.anyjob.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.anyjob.data.LoginRepository
import com.anyjob.data.Result

import com.anyjob.R

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(phoneNumber: String) {
        val result = loginRepository.login(phoneNumber)

        _loginResult.value = if (result is Result.Success)
            LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
        else
            LoginResult(error = R.string.login_failed)
    }

    fun validateLoginForm(phoneNumber: String) {
        _loginForm.value = if (isPhoneNumberValid(phoneNumber))
            LoginFormState(isDataValid = true)
        else
            LoginFormState(phoneNumberError = R.string.invalid_phone_number)
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