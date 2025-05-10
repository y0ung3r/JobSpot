package com.jobspot.domain.authorization.useCases

import com.jobspot.domain.profile.interfaces.WorkerFileRepository

class AddWorkerFilesUseCase(private val workerFileRepository: WorkerFileRepository) {
    suspend fun execute(userId: String, encodedInn: String?, encodedDiploma: String?, encodedEmploymentHistoryBook: String?) {
        workerFileRepository.addWorkerFiles(userId, encodedInn, encodedDiploma, encodedEmploymentHistoryBook)
    }
}