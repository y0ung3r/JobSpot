package com.anyjob.ui.authorization.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anyjob.R
import com.anyjob.data.authorization.PhoneNumberAuthorizationParameters
import com.anyjob.data.authorization.exceptions.AuthorizationException
import com.anyjob.data.authorization.exceptions.AuthorizationServerException
import com.anyjob.data.authorization.exceptions.InvalidCredentialsException
import com.anyjob.data.authorization.interfaces.PhoneNumberAuthorizationSource
import java.lang.IllegalArgumentException

class AuthorizationViewModel(private val authorizationSource: PhoneNumberAuthorizationSource) : ViewModel() {
    private val _isConfirmationCodeSent = MutableLiveData<Boolean>()
    val isConfirmationCodeSent: LiveData<Boolean> = _isConfirmationCodeSent

    private val _errorMessageCode = MutableLiveData<Int>()
    val errorMessageCode: LiveData<Int> = _errorMessageCode

    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String> = _phoneNumber

    fun sendVerificationCode(authorizationParameters: PhoneNumberAuthorizationParameters) {
        _phoneNumber.value = authorizationParameters.phoneNumber

        authorizationSource.setOnCodeSendingStateListener(object : PhoneNumberAuthorizationSource.OnCodeSendingStateListener {
            override fun onSuccess() {
                _isConfirmationCodeSent.value = true
            }

            override fun onFailed(exception: AuthorizationException) {
                _errorMessageCode.value = when (exception) {
                    is InvalidCredentialsException -> R.string.invalid_phone_number
                    is AuthorizationServerException -> R.string.authorization_server_error
                    else -> R.string.confirmation_code_send_failed
                }
            }
        })

        authorizationSource.sendCode(authorizationParameters)
    }

    fun verifyCode(code: String) {
        try {
            authorizationSource.verifyCode(code)
        }

        catch (exception: IllegalArgumentException) {
            _errorMessageCode.value = R.string.invalid_confirmation_code
        }

        catch (exception: AuthorizationException) {
            _errorMessageCode.value = R.string.unexpected_error
        }
    }
}