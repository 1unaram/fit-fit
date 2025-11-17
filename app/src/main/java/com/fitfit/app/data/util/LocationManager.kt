package com.fitfit.app.data.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class LocationManager(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // 위치 권한 확인
    fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Result<Location> {
        if (!hasLocationPermission()) {
            return Result.failure(Exception("No location permission granted"))
        }

        return try {
            val result = withTimeoutOrNull(10000L) {
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).await()
            }
            if (result != null) {
                Result.success(result)
            } else {
                Result.failure(Exception("Failed to get location: Timeout"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    data class Coordinates(
        val latitude: Double,
        val longitude: Double
    ) {
        companion object {
            fun fromLocation(location: Location): Coordinates {
                return Coordinates(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            }
        }
    }
}
