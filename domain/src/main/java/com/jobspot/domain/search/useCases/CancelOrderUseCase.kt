package com.jobspot.domain.search.useCases

import com.jobspot.domain.search.interfaces.OrderRepository

class CancelOrderUseCase(private val orderRepository: OrderRepository) {
    suspend fun execute(orderId: String) {
        orderRepository.cancelOrder(orderId)
    }
}