package com.anyjob.domain.search.interfaces

import com.anyjob.domain.search.OrderCreationParameters
import com.anyjob.domain.search.models.Order

interface OrderRepository {
    suspend fun createOrder(parameters: OrderCreationParameters): Order
    
    suspend fun setExecutor(orderId: String, workerId: String)

    suspend fun getExecutedOrder(): Order?

    suspend fun getOrder(orderId: String): Order?

    suspend fun excludeWorker(orderId: String, workerId: String)

    suspend fun cancelOrder(orderId: String)

    suspend fun finishOrder(orderId: String)

    suspend fun getAvailableOrders(): List<Order>

    suspend fun removeOrder(orderId: String)
}