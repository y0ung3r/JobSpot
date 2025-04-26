package com.anyjob.domain.profile.useCases

import com.anyjob.domain.authorization.interfaces.PhoneNumberAuthorizationProvider

class LogoutUseCase(private val authorizationProvider: PhoneNumberAuthorizationProvider) {
    fun execute() {
        return authorizationProvider.logout()
    }
}