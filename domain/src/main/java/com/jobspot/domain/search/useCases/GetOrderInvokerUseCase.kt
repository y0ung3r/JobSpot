package com.jobspot.domain.search.useCases

import com.jobspot.domain.profile.interfaces.UserRepository
import com.jobspot.domain.profile.models.User
import com.jobspot.domain.search.models.Order

class GetOrderInvokerUseCase(private val userRepository: UserRepository) {
    suspend fun execute(order: Order): User? {
        return userRepository.getUser(order.invokerId)
    }
}