package com.jobspot.domain.search.useCases

import com.jobspot.domain.search.interfaces.OrderRepository
import com.jobspot.domain.search.models.Order

class GetExecutedOrderUseCase(private val orderRepository: OrderRepository) {
    suspend fun execute(): Order? {
        return orderRepository.getExecutedOrder()
    }
}