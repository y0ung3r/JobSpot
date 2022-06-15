package com.anyjob.domain.search.interfaces

import com.anyjob.domain.search.models.Order

interface Finder {
    suspend fun searchWorker(order: Order)

    suspend fun searchClient()
}