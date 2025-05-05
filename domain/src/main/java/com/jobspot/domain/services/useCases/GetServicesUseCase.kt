package com.jobspot.domain.services.useCases

import com.jobspot.domain.services.interfaces.ServiceRepository
import com.jobspot.domain.services.models.Service

class GetServicesUseCase(private val serviceRepository: ServiceRepository) {
    suspend fun execute(): List<Service> {
        return serviceRepository.getAll()
    }
}