package com.anyjob.data.search

import android.location.Location
import com.anyjob.data.FirebaseContext
import com.anyjob.data.extensions.asList
import com.anyjob.data.extensions.get
import com.anyjob.data.extensions.remove
import com.anyjob.data.extensions.save
import com.anyjob.data.search.entities.OrderEntity
import com.anyjob.domain.authorization.interfaces.PhoneNumberAuthorizationProvider
import com.anyjob.domain.search.OrderCreationParameters
import com.anyjob.domain.search.interfaces.OrderRepository
import com.anyjob.domain.search.models.Order
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

internal class FirebaseOrderRepository(
    private val context: FirebaseContext,
    private val authorizationProvider: PhoneNumberAuthorizationProvider
) : OrderRepository {
    override suspend fun createOrder(parameters: OrderCreationParameters): Order {
        val orderId = UUID.randomUUID().toString()
        val order = OrderEntity().apply {
            id = orderId
            invokerId = parameters.invokerId
            address = parameters.address
            searchRadius = parameters.searchRadius
            service = parameters.service
        }

        context.orders.save(orderId, order)

        return Order(
            id = orderId,
            invokerId = parameters.invokerId,
            address = parameters.address,
            searchRadius = parameters.searchRadius,
            isCanceled = false,
            isFinished = false,
            service = parameters.service
        )
    }

    override suspend fun setExecutor(orderId: String, workerId: String) {
        val order = context.orders.get<OrderEntity>(orderId)

        if (order != null) {
            order.executorId = workerId
            context.orders.save(orderId, order)
        }
    }

    override suspend fun getExecutedOrder(): Order? {
        val currentUser = authorizationProvider.getAuthorizedUser() ?: return null
        val order = context.orders
            .asList<OrderEntity>()
            .find {
                !it.isCanceled && !it.isFinished && (it.executorId == currentUser.id || it.invokerId == currentUser.id)
            }
            ?: return null

        return Order(
            id = order.id!!,
            invokerId = order.invokerId!!,
            executorId = order.executorId,
            address = order.address!!,
            searchRadius = order.searchRadius!!,
            isCanceled = order.isCanceled,
            isFinished = order.isFinished,
            excludedExecutors = order.excludedExecutors,
            service = order.service!!
        )
    }

    override suspend fun getOrder(orderId: String): Order? {
        val order = context.orders.get<OrderEntity>(orderId) ?: return null

        return Order(
            id = order.id!!,
            invokerId = order.invokerId!!,
            executorId = order.executorId,
            address = order.address!!,
            searchRadius = order.searchRadius!!,
            isCanceled = order.isCanceled,
            isFinished = order.isFinished,
            excludedExecutors = order.excludedExecutors,
            service = order.service!!
        )
    }

    override suspend fun excludeWorker(orderId: String, workerId: String) {
        val order = context.orders.get<OrderEntity>(orderId)

        if (order != null) {
            order.excludedExecutors.add(workerId)
            context.orders.save(orderId, order)
        }
    }

    override suspend fun cancelOrder(orderId: String) {
        val order = context.orders.get<OrderEntity>(orderId)

        if (order != null) {
            order.isCanceled = true
            context.orders.save(orderId, order)
        }
    }

    override suspend fun finishOrder(orderId: String) {
        val order = context.orders.get<OrderEntity>(orderId)

        if (order != null) {
            order.isFinished = true
            context.orders.save(orderId, order)
        }
    }

    override suspend fun getAvailableOrders(): List<Order> {
        val currentUser = authorizationProvider.getAuthorizedUser() ?: return emptyList()
        val workerId = currentUser.id
        val availableOrders = context.orders
            .asList<OrderEntity>()
            .asSequence()
            .filterNot {
                it.isCanceled
            }
            .filterNot {
                it.isFinished
            }
            .filterNot {
                it.invokerId.equals(workerId)
            }
            .filter {
                it.executorId == null
            }
            .filter {
                val workerAddress = Location("").apply {
                    latitude = currentUser.geolocation!!.latitude
                    longitude = currentUser.geolocation!!.longitude
                }

                val orderAddress = Location("").apply {
                    latitude = it.address!!.latitude
                    longitude = it.address!!.longitude
                }

                val searchRadius = it.searchRadius!!

                return@filter workerAddress.distanceTo(orderAddress) <= searchRadius
            }
            .filterNot {
                it.excludedExecutors.contains(workerId)
            }
            .toList()

        return availableOrders.map {
            Order(
                id = it.id!!,
                invokerId = it.invokerId!!,
                address = it.address!!,
                searchRadius = it.searchRadius!!,
                isCanceled = it.isCanceled,
                isFinished = it.isFinished,
                excludedExecutors = it.excludedExecutors,
                service = it.service!!
            )
        }
    }

    override suspend fun removeOrder(orderId: String) {
        context.orders.remove(orderId)
    }
}