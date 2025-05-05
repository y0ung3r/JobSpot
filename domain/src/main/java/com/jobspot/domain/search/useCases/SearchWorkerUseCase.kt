package com.jobspot.domain.search.useCases

import com.jobspot.domain.profile.models.User
import com.jobspot.domain.search.OrderCreationParameters
import com.jobspot.domain.search.interfaces.WorkerFinder
import com.jobspot.domain.search.interfaces.OrderRepository
import com.jobspot.domain.search.models.Order

class SearchWorkerUseCase(private val orderRepository: OrderRepository, private val finder: WorkerFinder) {
    suspend fun execute(parameters: OrderCreationParameters, onWorkerFound: (User) -> Unit): Order {
        val order = orderRepository.createOrder(parameters)
        finder.start(order, onWorkerFound)
        return order
    }
}