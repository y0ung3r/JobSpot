package com.anyjob.ui.authorization.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anyjob.domain.authorization.PhoneNumberAuthorizationParameters
import com.anyjob.domain.authorization.useCases.ResendVerificationCodeUseCase
import com.anyjob.domain.authorization.useCases.SendVerificationCodeUseCase
import com.anyjob.domain.authorization.useCases.VerifyCodeUseCase
import kotlinx.coroutines.launch

class AuthorizationViewModel(
    private val sendVerificationCodeUseCase: SendVerificationCodeUseCase,
    private val resendVerificationCodeUseCase: ResendVerificationCodeUseCase,
    private val verifyCodeUseCase: VerifyCodeUseCase
) : ViewModel() {
    private val _onCodeSent = MutableLiveData<Result<Unit>>()
    val onCodeSent: LiveData<Result<Unit>> = _onCodeSent

    private val _onCodeResent = MutableLiveData<Result<Unit>>()
    val onCodeResent: LiveData<Result<Unit>> = _onCodeResent

    private val _onCodeVerified = MutableLiveData<Result<Unit>>()
    val onCodeVerified: LiveData<Result<Unit>> = _onCodeVerified

    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String> = _phoneNumber

    private val _resendTimeout = MutableLiveData<Long>()
    val resendTimeout: LiveData<Long> = _resendTimeout

    fun sendVerificationCode(authorizationParameters: PhoneNumberAuthorizationParameters) {
        _phoneNumber.value = authorizationParameters.phoneNumber

        try {
            sendVerificationCodeUseCase.execute(authorizationParameters) { sentResult ->
                _onCodeSent.value = sentResult
            }
        }

        catch (exception: Exception) {
            _onCodeSent.value = Result.failure(exception)
        }
    }

    fun resendVerificationCode() {
        try {
            resendVerificationCodeUseCase.execute { resentResult ->
                _onCodeResent.value = resentResult
            }
        }

        catch (exception: Exception) {
            _onCodeResent.value = Result.failure(exception)
        }
    }

    fun verifyCode(code: String) {
        viewModelScope.launch {
            try {
                verifyCodeUseCase.execute(code)
                _onCodeVerified.value = Result.success(Unit)
            }

            catch (exception: Exception) {
                _onCodeVerified.value = Result.failure(exception)
            }
        }
    }
}