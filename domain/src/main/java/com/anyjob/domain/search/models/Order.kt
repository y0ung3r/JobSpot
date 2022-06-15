package com.anyjob.domain.search.models

import com.anyjob.domain.profile.models.MapsAddress

data class Order(
    val id: String,
    val invokerId: String,
    val address: MapsAddress,
    val searchRadius: Double
)