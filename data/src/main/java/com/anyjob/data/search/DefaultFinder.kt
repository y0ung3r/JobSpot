package com.anyjob.data.search

import com.anyjob.domain.profile.interfaces.UserRepository
import com.anyjob.domain.profile.models.User
import com.anyjob.domain.search.interfaces.Finder
import com.anyjob.domain.search.models.Order
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil

class DefaultFinder(private val userRepository: UserRepository) : Finder {
    override suspend fun searchWorker(order: Order) {
        val workers = userRepository.getFreeWorkers()
        var existsWorker: User? = null

        while (existsWorker != null) {
            existsWorker = workers.find { worker ->
                val workerAddress = worker.address!!
                val distance = SphericalUtil.computeDistanceBetween(
                    LatLng(workerAddress.latitude, workerAddress.longitude),
                    LatLng(order.address.latitude, order.address.longitude)
                )

                return@find distance <= order.searchRadius
            }
        }
    }

    override suspend fun searchClient() {
        TODO("Not yet implemented")
    }
}