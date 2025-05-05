package com.jobspot.data.services

import com.jobspot.data.FirebaseContext
import com.jobspot.data.extensions.asList
import com.jobspot.data.services.entities.ServiceEntity
import com.jobspot.domain.services.interfaces.ServiceRepository
import com.jobspot.domain.services.models.Service

internal class FirebaseServiceRepository(private val context: FirebaseContext) : ServiceRepository {
    override suspend fun getAll(): List<Service> {
        val storeServices = context.services.asList<ServiceEntity>()

        return storeServices.map {
            Service(
                id = it.id,
                category = it.category,
                title = it.title
            )
        }
    }
}