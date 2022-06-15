package com.anyjob.domain.search.interfaces

import com.anyjob.domain.search.OrderCreationParameters
import com.anyjob.domain.search.models.Order

interface OrderRepository {
    suspend fun createOrder(parameters: OrderCreationParameters): Order

    suspend fun cancelOrder(orderId: String)
}