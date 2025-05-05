package com.jobspot.domain.profile.useCases

import com.jobspot.domain.authorization.interfaces.PhoneNumberAuthorizationProvider

class LogoutUseCase(private val authorizationProvider: PhoneNumberAuthorizationProvider) {
    fun execute() {
        return authorizationProvider.logout()
    }
}