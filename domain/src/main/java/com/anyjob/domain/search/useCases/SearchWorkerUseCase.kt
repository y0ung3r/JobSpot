package com.anyjob.domain.search.useCases

import com.anyjob.domain.profile.models.User
import com.anyjob.domain.search.OrderCreationParameters
import com.anyjob.domain.search.interfaces.WorkerFinder
import com.anyjob.domain.search.interfaces.OrderRepository

class SearchWorkerUseCase(private val orderRepository: OrderRepository, private val finder: WorkerFinder) {
    suspend fun execute(parameters: OrderCreationParameters, onWorkerFound: (User) -> Unit): String {
        val order = orderRepository.createOrder(parameters)
        finder.start(order, onWorkerFound)
        return order.id
    }
}