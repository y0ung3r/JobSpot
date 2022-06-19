package com.anyjob.domain.services.interfaces

import com.anyjob.domain.services.models.Service

interface ServiceRepository {
    suspend fun getAll(): List<Service>
}