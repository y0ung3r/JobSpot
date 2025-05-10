package com.jobspot.data.search

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationServices
import com.jobspot.data.extensions.isGeolocationPermissionsDenied
import com.jobspot.domain.profile.interfaces.UserRepository
import com.jobspot.domain.profile.models.MapAddress
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class DefaultGeolocationUpdater(private val applicationContext: Context, private val userRepository: UserRepository) {
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private suspend fun internalStart(userId: String) {
        while(true) {
            delay(5000)

            if (applicationContext.isGeolocationPermissionsDenied())
                continue

            val locationProvider = LocationServices.getFusedLocationProviderClient(applicationContext)

            locationProvider.lastLocation.addOnSuccessListener {
                if (it != null) {
                    GlobalScope.async {
                        userRepository.updateGeolocation(
                            userId,
                            MapAddress(it.latitude, it.longitude)
                        )
                    }
                }
            }
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun start(userId: String) {
        GlobalScope.async {
            internalStart(userId)
        }
    }
}