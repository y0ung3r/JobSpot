package com.anyjob.domain.authorization.useCases

import com.anyjob.domain.authorization.interfaces.PhoneNumberAuthorizationProvider

class ResendVerificationCodeUseCase(private val authorizationProvider: PhoneNumberAuthorizationProvider) {
    fun execute(onCodeResent: (Result<Unit>) -> Unit) {
        authorizationProvider.resendCode(onCodeResent)
    }
}