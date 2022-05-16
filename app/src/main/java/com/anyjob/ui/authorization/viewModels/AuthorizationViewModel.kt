package com.anyjob.ui.authorization.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anyjob.domain.authorization.PhoneNumberAuthorizationParameters
import com.anyjob.domain.authorization.exceptions.AuthorizationException
import com.anyjob.domain.authorization.useCases.SendVerificationCodeUseCase
import com.anyjob.domain.authorization.useCases.VerifyCodeUseCase
import java.lang.IllegalArgumentException

class AuthorizationViewModel(private val sendVerificationCodeUseCase: SendVerificationCodeUseCase, private val verifyCodeUseCase: VerifyCodeUseCase) : ViewModel() {
    private val _onCodeSent = MutableLiveData<Result<Boolean>>()
    val onCodeSent: LiveData<Result<Boolean>> = _onCodeSent

    private val _onCodeVerified = MutableLiveData<Result<Unit>>()
    val onCodeVerified: LiveData<Result<Unit>> = _onCodeVerified

    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String> = _phoneNumber

    private val _resendTimeout = MutableLiveData<Long>()
    val resendTimeout: LiveData<Long> = _resendTimeout

    fun sendVerificationCode(authorizationParameters: PhoneNumberAuthorizationParameters) {
        _phoneNumber.value = authorizationParameters.phoneNumber

        sendVerificationCodeUseCase.execute(authorizationParameters) { sentResult ->
            _onCodeSent.value = sentResult
        }
    }

    fun verifyCode(code: String) {
        try {
            verifyCodeUseCase.execute(code) { verifiedResult ->
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