package com.anyjob.domain.authorization.useCases

import com.anyjob.domain.authorization.ProfileCreationParameters
import com.anyjob.domain.profile.interfaces.UserRepository

class CreateProfileUseCase(private val userRepository: UserRepository) {
    suspend fun execute(parameters: ProfileCreationParameters) {
        userRepository.createProfile(parameters)
    }
}