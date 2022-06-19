package com.anyjob.domain.search.useCases

import com.anyjob.domain.authorization.interfaces.PhoneNumberAuthorizationProvider
import com.anyjob.domain.search.interfaces.OrderRepository
import com.anyjob.domain.search.models.Order

class AcceptJobUseCase(private val authorizationProvider: PhoneNumberAuthorizationProvider, private val orderRepository: OrderRepository) {
    suspend fun execute(order: Order) {
        val user = authorizationProvider.getAuthorizedUser() ?: return
        orderRepository.setExecutor(order.id, user.id)
    }
}