package com.jobspot.domain.profile.interfaces

interface WorkerFileRepository {
    suspend fun addWorkerFiles(userId: String, encodedInn: String?, encodedDiploma: String?, encodedEmploymentHistoryBook: String?)
}