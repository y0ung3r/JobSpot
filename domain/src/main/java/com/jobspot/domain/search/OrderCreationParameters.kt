package com.jobspot.domain.search

import com.jobspot.domain.profile.models.MapAddress
import com.jobspot.domain.services.models.Service

class OrderCreationParameters(
    val invokerId: String,
    val address: MapAddress,
    val searchRadius: Double,
    val service: Service
)