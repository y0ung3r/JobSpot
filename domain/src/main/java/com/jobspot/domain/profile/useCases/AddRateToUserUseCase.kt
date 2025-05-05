package com.jobspot.domain.profile.useCases

import com.jobspot.domain.profile.interfaces.UserRepository

class AddRateToUserUseCase(private val userRepository: UserRepository) {
    suspend fun execute(userId: String, rate: Float) {
        userRepository.addRateToUser(userId, rate)
    }
}