package com.jobspot.domain.authorization.useCases

import com.jobspot.domain.authorization.ProfileCreationParameters
import com.jobspot.domain.profile.interfaces.UserRepository

class CreateProfileUseCase(private val userRepository: UserRepository) {
    suspend fun execute(parameters: ProfileCreationParameters) {
        userRepository.createProfile(parameters)
    }
}