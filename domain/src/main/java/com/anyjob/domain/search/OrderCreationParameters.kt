package com.anyjob.domain.search

import com.anyjob.domain.profile.models.MapsAddress

class OrderCreationParameters(
    val invokerId: String,
    val address: MapsAddress,
    val searchRadius: Double
)