package com.jobspot.data.search

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.jobspot.domain.profile.interfaces.UserRepository
import org.koin.core.Koin

class GeolocationUpdaterFactory(private val koin: Koin) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        if (workerClassName == GeolocationUpdater::class.java.name)
            return GeolocationUpdater(appContext, workerParameters, koin.get<UserRepository>())

        return null
    }
}