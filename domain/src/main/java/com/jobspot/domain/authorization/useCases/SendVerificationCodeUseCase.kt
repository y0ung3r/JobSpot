package com.jobspot.domain.authorization.useCases

import com.jobspot.domain.authorization.PhoneNumberAuthorizationParameters
import com.jobspot.domain.authorization.interfaces.PhoneNumberAuthorizationProvider

class SendVerificationCodeUseCase(private val authorizationProvider: PhoneNumberAuthorizationProvider) {
    fun execute(authorizationParameters: PhoneNumberAuthorizationParameters, onCodeSent: (Result<Unit>) -> Unit) {
        authorizationProvider.sendCode(authorizationParameters, onCodeSent)
    }
}