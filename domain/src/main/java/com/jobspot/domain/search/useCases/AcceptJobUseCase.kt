package com.jobspot.domain.search.useCases

import com.jobspot.domain.authorization.interfaces.PhoneNumberAuthorizationProvider
import com.jobspot.domain.search.interfaces.OrderRepository
import com.jobspot.domain.search.models.Order

class AcceptJobUseCase(private val authorizationProvider: PhoneNumberAuthorizationProvider, private val orderRepository: OrderRepository) {
    suspend fun execute(order: Order) {
        val user = authorizationProvider.getAuthorizedUser() ?: return
        orderRepository.setExecutor(order.id, user.id)
    }
}