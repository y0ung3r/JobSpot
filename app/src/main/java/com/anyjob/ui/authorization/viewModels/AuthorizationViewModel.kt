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
    private val _isCodeSent = MutableLiveData<Boolean>()
    val isCodeSent: LiveData<Boolean> = _isCodeSent

    private val _isCodeVerified = MutableLiveData<Boolean>()
    val isCodeVerified: LiveData<Boolean> = _isCodeVerified

    private val _errorMessageCode = MutableLiveData<Int>()
    val errorMessageCode: LiveData<Int> = _errorMessageCode

    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String> = _phoneNumber

    fun sendVerificationCode(authorizationParameters: PhoneNumberAuthorizationParameters) {
        _phoneNumber.value = authorizationParameters.phoneNumber

        authorizationProvider.sendCode(authorizationParameters) { result ->
            result.onSuccess {
                _isCodeSent.value = true
            }
            .onFailure { exception ->
                _errorMessageCode.value = when (exception) {
                    is InvalidCredentialsException -> R.string.invalid_phone_number_format
                    is AuthorizationServerException -> R.string.authorization_server_error
                    else -> R.string.confirmation_code_send_failed
                }
            }
        }
    }

    fun verifyCode(code: String) {
        try {
            authorizationProvider.verifyCode(code) { result ->
                result.onSuccess {
                    _isCodeVerified.value = true
                }
                .onFailure { exception ->
                    _errorMessageCode.value = when (exception) {
                        is InvalidCredentialsException -> R.string.incorrect_confirmation_code
                        else -> R.string.confirmation_code_verification_error
                    }
                }
            }
        }

        catch (exception: IllegalArgumentException) {
            _errorMessageCode.value = R.string.invalid_confirmation_code_format
        }

        catch (exception: AuthorizationException) {
            _errorMessageCode.value = R.string.unexpected_error
        }
    }
}