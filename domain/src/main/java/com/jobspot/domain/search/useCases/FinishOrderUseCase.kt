package com.jobspot.domain.search.useCases

import com.jobspot.domain.search.interfaces.OrderRepository

class FinishOrderUseCase(private val orderRepository: OrderRepository) {
    suspend fun execute(orderId: String) {
        orderRepository.finishOrder(orderId)
    }
}