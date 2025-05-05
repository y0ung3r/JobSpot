package com.jobspot.domain.search.useCases

import com.jobspot.domain.search.interfaces.OrderRepository
import com.jobspot.domain.search.interfaces.WorkerFinder

class CancelSearchUseCase(private val orderRepository: OrderRepository, private val finder: WorkerFinder) {
    suspend fun execute(orderId: String) {
        finder.stop()
        orderRepository.removeOrder(orderId)
    }
}