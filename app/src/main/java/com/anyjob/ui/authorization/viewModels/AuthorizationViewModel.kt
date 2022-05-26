package com.anyjob.ui.authorization.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anyjob.domain.authorization.PhoneNumberAuthorizationParameters
import com.anyjob.domain.authorization.useCases.ResendVerificationCodeUseCase
import com.anyjob.domain.authorization.useCases.SendVerificationCodeUseCase
import com.anyjob.domain.authorization.useCases.VerifyCodeUseCase
import com.anyjob.domain.profile.models.User
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
        _phoneNumber.postValue(authorizationParameters.phoneNumber)
        _resendTimeout.postValue(authorizationParameters.timeout)

        sendVerificationCodeUseCase.execute(authorizationParameters) {
            _onCodeSent.postValue(it)
        }
    }

    fun resendVerificationCode() {
        resendVerificationCodeUseCase.execute {
            _onCodeResent.postValue(it)
        }
    }

    fun verifyCode(code: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                verifyCodeUseCase.execute(code)
            }
            .onSuccess {
                _onCodeVerified.postValue(
                    Result.success(Unit)
                )
            }
            .onFailure { exception ->
                _onCodeVerified.postValue(
                    Result.failure(exception)
                )
            }
        }
    }
}