package com.anyjob.domain.search.interfaces

import com.anyjob.domain.profile.models.User
import com.anyjob.domain.search.models.Order

interface WorkerFinder {
    suspend fun start(order: Order, onWorkerFound: (User) -> Unit)

    fun stop()
}