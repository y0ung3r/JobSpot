package com.anyjob.domain.search

import com.anyjob.domain.profile.models.MapAddress
import com.anyjob.domain.services.models.Service

class OrderCreationParameters(
    val invokerId: String,
    val address: MapAddress,
    val searchRadius: Double,
    val service: Service
)