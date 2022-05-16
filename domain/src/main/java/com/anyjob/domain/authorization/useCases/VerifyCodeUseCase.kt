package com.anyjob.domain.authorization.useCases

import com.anyjob.domain.authorization.interfaces.PhoneNumberAuthorizationProvider

class VerifyCodeUseCase(private val authorizationProvider: PhoneNumberAuthorizationProvider) {
    fun execute(code: String, onCodeVerified: (Result<Unit>) -> Unit) {
        authorizationProvider.verifyCode(code, onCodeVerified)
    }
}