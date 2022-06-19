package com.anyjob.domain.search.useCases

import com.anyjob.domain.search.interfaces.OrderRepository
import com.anyjob.domain.search.models.Order

class GetExecutedOrderUseCase(private val orderRepository: OrderRepository) {
    suspend fun execute(): Order? {
        return orderRepository.getExecutedOrder()
    }
}