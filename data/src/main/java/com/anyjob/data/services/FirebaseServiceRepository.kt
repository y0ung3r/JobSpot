package com.anyjob.data.services

import com.anyjob.data.FirebaseContext
import com.anyjob.data.extensions.asList
import com.anyjob.data.services.entities.ServiceEntity
import com.anyjob.domain.services.interfaces.ServiceRepository
import com.anyjob.domain.services.models.Service

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