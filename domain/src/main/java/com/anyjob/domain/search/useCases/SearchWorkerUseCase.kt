package com.anyjob.domain.search.useCases

import com.anyjob.domain.search.OrderCreationParameters
import com.anyjob.domain.search.interfaces.Finder
import com.anyjob.domain.search.interfaces.OrderRepository

class SearchWorkerUseCase(private val orderRepository: OrderRepository, private val finder: Finder) {
    suspend fun execute(parameters: OrderCreationParameters) {
        val order = orderRepository.createOrder(parameters)
        finder.searchWorker(order)
    }
}