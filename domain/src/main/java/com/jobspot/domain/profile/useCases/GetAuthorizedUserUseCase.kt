package com.jobspot.domain.profile.useCases

import com.jobspot.domain.authorization.interfaces.PhoneNumberAuthorizationProvider
import com.jobspot.domain.profile.models.User

class GetAuthorizedUserUseCase(private val authorizationProvider: PhoneNumberAuthorizationProvider) {
    suspend fun execute(): User? {
        return authorizationProvider.getAuthorizedUser()
    }
}