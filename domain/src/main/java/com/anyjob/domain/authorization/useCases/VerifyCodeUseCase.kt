package com.anyjob.domain.authorization.useCases

import com.anyjob.domain.authorization.interfaces.PhoneNumberAuthorizationProvider

class VerifyCodeUseCase(private val authorizationProvider: PhoneNumberAuthorizationProvider) {
    suspend fun execute(code: String) {
        authorizationProvider.verifyCode(code)
    }
}