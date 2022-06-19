package com.anyjob.domain.search.models

import com.anyjob.domain.profile.models.MapsAddress
import com.anyjob.domain.services.models.Service

data class Order(
    val id: String,
    val invokerId: String,
    val executorId: String? = null,
    val address: MapsAddress,
    val searchRadius: Double,
    var isCanceled: Boolean = false,
    var isFinished: Boolean = false,
    val excludedExecutors: List<String> = emptyList(),
    val service: Service
)