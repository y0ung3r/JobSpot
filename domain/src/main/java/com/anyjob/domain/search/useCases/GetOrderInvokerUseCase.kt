package com.anyjob.domain.search.useCases

import com.anyjob.domain.profile.interfaces.UserRepository
import com.anyjob.domain.profile.models.User
import com.anyjob.domain.search.models.Order

class GetOrderInvokerUseCase(private val userRepository: UserRepository) {
    suspend fun execute(order: Order): User? {
        return userRepository.getUser(order.invokerId)
    }
}