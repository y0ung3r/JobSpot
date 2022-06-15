package com.anyjob.data.search

import com.anyjob.data.FirebaseContext
import com.anyjob.data.extensions.remove
import com.anyjob.data.extensions.save
import com.anyjob.data.search.entities.OrderEntity
import com.anyjob.domain.search.OrderCreationParameters
import com.anyjob.domain.search.interfaces.OrderRepository
import com.anyjob.domain.search.models.Order
import java.util.*

internal class FirebaseOrderRepository(private val context: FirebaseContext) : OrderRepository {
    override suspend fun createOrder(parameters: OrderCreationParameters): Order {
        val orderId = UUID.randomUUID().toString()
        val order = OrderEntity().apply {
            id = orderId
            invokerId = parameters.invokerId
            address = parameters.address
            searchRadius = parameters.searchRadius
        }

        context.orders.save(orderId, order)

        return Order(
            id = orderId,
            invokerId = parameters.invokerId,
            address = parameters.address,
            searchRadius = parameters.searchRadius
        )
    }

    override suspend fun cancelOrder(orderId: String) {
        context.orders.remove(orderId)
    }
}