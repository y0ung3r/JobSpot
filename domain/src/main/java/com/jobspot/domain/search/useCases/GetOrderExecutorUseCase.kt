package com.jobspot.domain.search.useCases

import com.jobspot.domain.profile.interfaces.UserRepository
import com.jobspot.domain.profile.models.User
import com.jobspot.domain.search.models.Order

class GetOrderExecutorUseCase(private val userRepository: UserRepository) {
    suspend fun execute(order: Order): User? {
        val executorId = order.executorId ?: return null
        return userRepository.getUser(executorId)
    }
}