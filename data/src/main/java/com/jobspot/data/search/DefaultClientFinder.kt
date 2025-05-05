package com.jobspot.data.search

import com.jobspot.domain.authorization.interfaces.PhoneNumberAuthorizationProvider
import com.jobspot.domain.search.interfaces.ClientFinder
import com.jobspot.domain.search.interfaces.OrderRepository
import com.jobspot.domain.search.models.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class DefaultClientFinder(
    private val orderRepository: OrderRepository,
    private val authorizationProvider: PhoneNumberAuthorizationProvider
) : ClientFinder {
    private suspend fun internalStart(onClientFound: (Order) -> Unit) {
        val worker = authorizationProvider.getAuthorizedUser() ?: return

        while (true) {
            val availableOrders = orderRepository.getAvailableOrders()

            if (availableOrders.any()) {
                val order = availableOrders.first()

                withContext(Dispatchers.Main) {
                    onClientFound(order)
                }

                orderRepository.excludeWorker(order.id, worker.id)

                break
            }
        }
    }

    override fun start(onClientFound: (Order) -> Unit) {
        GlobalScope.async {
            internalStart(onClientFound)
        }
    }
}