package com.anyjob.ui.authorization.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anyjob.R
import com.anyjob.data.authorization.PhoneNumberAuthorizationParameters
import com.anyjob.data.authorization.exceptions.AuthorizationException
import com.anyjob.data.authorization.exceptions.AuthorizationServerException
import com.anyjob.data.authorization.exceptions.InvalidCredentialsException
import com.anyjob.data.authorization.interfaces.PhoneNumberAuthorizationProvider
import java.lang.IllegalArgumentException

class AuthorizationViewModel(private val authorizationProvider: PhoneNumberAuthorizationProvider) : ViewModel() {
    private val _onCodeSent = MutableLiveData<Result<Boolean>>()
    val onCodeSent: LiveData<Result<Boolean>> = _onCodeSent

    private val _onCodeVerified = MutableLiveData<Result<Unit>>()
    val onCodeVerified: LiveData<Result<Unit>> = _onCodeVerified

    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String> = _phoneNumber

    fun sendVerificationCode(authorizationParameters: PhoneNumberAuthorizationParameters) {
        _phoneNumber.value = authorizationParameters.phoneNumber

        authorizationProvider.sendCode(authorizationParameters) { sentResult ->
            _onCodeSent.value = sentResult
        }
    }

    fun verifyCode(code: String) {
        try {
            authorizationProvider.verifyCode(code) { verifiedResult ->
                _onCodeVerified.value = verifiedResult
            }
        }

        catch (exception: IllegalArgumentException) {
            _onCodeVerified.value = Result.failure(exception)
        }

        catch (exception: AuthorizationException) {
            _onCodeVerified.value = Result.failure(exception)
        }
    }
}