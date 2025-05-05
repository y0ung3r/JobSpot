package com.jobspot.domain.search.interfaces

import com.jobspot.domain.profile.models.User
import com.jobspot.domain.search.models.Order

interface WorkerFinder {
    fun start(order: Order, onWorkerFound: (User) -> Unit)

    fun stop()
}