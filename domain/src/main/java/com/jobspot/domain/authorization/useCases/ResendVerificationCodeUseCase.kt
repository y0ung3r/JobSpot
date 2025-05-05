package com.jobspot.domain.authorization.useCases

import com.jobspot.domain.authorization.interfaces.PhoneNumberAuthorizationProvider

class ResendVerificationCodeUseCase(private val authorizationProvider: PhoneNumberAuthorizationProvider) {
    fun execute(onCodeResent: (Result<Unit>) -> Unit) {
        authorizationProvider.resendCode(onCodeResent)
    }
}