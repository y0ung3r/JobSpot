package com.anyjob.domain.profile.useCases

import com.anyjob.domain.authorization.interfaces.PhoneNumberAuthorizationProvider
import com.anyjob.domain.profile.models.User

class GetAuthorizedUserUseCase(private val authorizationProvider: PhoneNumberAuthorizationProvider) {
    suspend fun execute(): User? {
        return authorizationProvider.getAuthorizedUser()
    }
}