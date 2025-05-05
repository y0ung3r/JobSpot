package com.jobspot.data.search

import com.jobspot.domain.profile.interfaces.UserRepository
import com.jobspot.domain.profile.models.User
import com.jobspot.domain.search.interfaces.OrderRepository
import com.jobspot.domain.search.interfaces.WorkerFinder
import com.jobspot.domain.search.models.Order
import kotlinx.coroutines.*

internal class DefaultWorkerFinder(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) : WorkerFinder {
    private var _stopped: Boolean = true

    private suspend fun internalStart(order: Order, onWorkerFound: (User) -> Unit) {
        var foundWorker: User? = null

        while (foundWorker == null && !_stopped) {
            val updatedOrder = orderRepository.getOrder(order.id)
            val executorId = updatedOrder?.executorId

            if (executorId != null) {
                foundWorker = userRepository.getUser(executorId)
            }
        }

        if (foundWorker != null) {
            withContext(Dispatchers.Main) {
                onWorkerFound.invoke(foundWorker)
            }
        }
    }

    override fun start(order: Order, onWorkerFound: (User) -> Unit) {
        _stopped = false

        GlobalScope.async {
            internalStart(order, onWorkerFound)
        }
    }

    override fun stop() {
        _stopped = true
    }
}