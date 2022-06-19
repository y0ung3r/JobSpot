package com.anyjob.domain.services.useCases

import com.anyjob.domain.services.interfaces.ServiceRepository
import com.anyjob.domain.services.models.Service

class GetServicesUseCase(private val serviceRepository: ServiceRepository) {
    suspend fun execute(): List<Service> {
        return serviceRepository.getAll()
    }
}