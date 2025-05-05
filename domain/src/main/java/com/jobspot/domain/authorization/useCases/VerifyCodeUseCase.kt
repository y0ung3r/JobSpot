package com.jobspot.domain.authorization.useCases

import com.jobspot.domain.authorization.interfaces.PhoneNumberAuthorizationProvider

class VerifyCodeUseCase(private val authorizationProvider: PhoneNumberAuthorizationProvider) {
    suspend fun execute(code: String) {
        authorizationProvider.verifyCode(code)
    }
}