package com.anyjob.data.search

import com.anyjob.domain.profile.interfaces.UserRepository
import com.anyjob.domain.profile.models.User
import com.anyjob.domain.search.interfaces.WorkerFinder
import com.anyjob.domain.search.models.Order
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.coroutineScope

internal class DefaultWorkerFinder(private val userRepository: UserRepository) : WorkerFinder {
    private var _stopped: Boolean = true

    private suspend fun internalStart(order: Order, onWorkerFound: (User) -> Unit) {
        val workers = userRepository.getAvailableWorkers()
        var foundWorker: User? = null

        while (foundWorker == null && !_stopped) {
            foundWorker = workers.find { worker ->
                val workerAddress = worker.address!!
                val distance = SphericalUtil.computeDistanceBetween(
                    LatLng(workerAddress.latitude, workerAddress.longitude),
                    LatLng(order.address.latitude, order.address.longitude)
                )

                return@find distance <= order.searchRadius
            }
        }

        if (foundWorker != null) {
            onWorkerFound(foundWorker)
        }
    }

    override suspend fun start(order: Order, onWorkerFound: (User) -> Unit) {
        _stopped = false

        coroutineScope {
            internalStart(order, onWorkerFound)
        }
    }

    override fun stop() {
        _stopped = true
    }
}