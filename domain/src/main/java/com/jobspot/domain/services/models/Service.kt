package com.jobspot.domain.services.models;

import java.io.Serializable

data class Service(
    val id: String? = null,
    val category: String? = null,
    val title: String? = null
) : Serializable