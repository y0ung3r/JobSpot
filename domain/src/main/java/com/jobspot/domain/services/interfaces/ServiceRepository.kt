package com.jobspot.domain.services.interfaces

import com.jobspot.domain.services.models.Service

interface ServiceRepository {
    suspend fun getAll(): List<Service>
}