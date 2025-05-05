package com.jobspot.data.search

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.jobspot.data.extensions.isGeolocationPermissionsDenied
import com.jobspot.domain.profile.interfaces.UserRepository
import com.jobspot.domain.profile.models.MapAddress
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.concurrent.TimeUnit

class GeolocationUpdater(appContext: Context, workerParams: WorkerParameters, private val userRepository: UserRepository)
    : Worker(appContext, workerParams) {
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun doWork(): Result {
        val userId = inputData.getString("USER_ID")
            ?: return Result.success()

        if (applicationContext.isGeolocationPermissionsDenied())
            return rescheduleWork(userId)

        val locationProvider = LocationServices.getFusedLocationProviderClient(applicationContext)

        locationProvider.lastLocation.addOnSuccessListener {
            if (it != null) {
                GlobalScope.async {
                    userRepository.updateGeolocation(userId, MapAddress(it.latitude, it.longitude))
                }
            }
        }

        return rescheduleWork(userId)
    }

    private fun rescheduleWork(userId: String): Result {
        val geolocationUpdateRequest = OneTimeWorkRequestBuilder<GeolocationUpdater>()
            .setInputData(workDataOf("USER_ID" to userId))
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()

        WorkManager
            .getInstance(applicationContext)
            .enqueue(geolocationUpdateRequest)

        return Result.success()
    }
}