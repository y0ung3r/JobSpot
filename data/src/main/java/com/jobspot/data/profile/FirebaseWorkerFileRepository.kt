package com.jobspot.data.profile

import com.jobspot.data.FirebaseContext
import com.jobspot.data.extensions.save
import com.jobspot.data.profile.entities.WorkerFileEntity
import com.jobspot.domain.profile.interfaces.WorkerFileRepository

internal class FirebaseWorkerFileRepository(private val context: FirebaseContext) : WorkerFileRepository {
    override suspend fun addWorkerFiles(userId: String, encodedInn: String?, encodedDiploma: String?, encodedEmploymentHistoryBook: String?) {
        if (encodedInn == null || encodedDiploma == null || encodedEmploymentHistoryBook == null)
            return

        val storeWorkerFile = WorkerFileEntity().apply {
            workerId = userId
            this.encodedInn = encodedInn
            this.encodedDiploma = encodedDiploma
            this.encodedEmploymentHistoryBook = encodedEmploymentHistoryBook
        }

        context.workerFiles.save(userId, storeWorkerFile)
    }
}