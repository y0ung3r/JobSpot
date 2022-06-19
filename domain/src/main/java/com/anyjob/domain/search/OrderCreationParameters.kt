package com.anyjob.domain.search

import com.anyjob.domain.profile.models.MapsAddress
import com.anyjob.domain.services.models.Service

class OrderCreationParameters(
    val invokerId: String,
    val address: MapsAddress,
    val searchRadius: Double,
    val service: Service
)