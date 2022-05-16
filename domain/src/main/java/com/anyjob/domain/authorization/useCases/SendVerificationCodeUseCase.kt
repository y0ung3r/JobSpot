package com.anyjob.domain.authorization.useCases

import com.anyjob.domain.authorization.PhoneNumberAuthorizationParameters
import com.anyjob.domain.authorization.interfaces.PhoneNumberAuthorizationProvider

class SendVerificationCodeUseCase(private val authorizationProvider: PhoneNumberAuthorizationProvider) {
    fun execute(authorizationParameters: PhoneNumberAuthorizationParameters, onCodeSent: (Result<Boolean>) -> Unit) {
        authorizationProvider.sendCode(authorizationParameters, onCodeSent)
    }
}