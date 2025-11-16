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
            // 1. 마지막 위치 시도 (5분 이내의 최근 위치만 사용)
            val lastLocation = fusedLocationClient.lastLocation.await()
            if (lastLocation != null) {
                val locationAge = System.currentTimeMillis() - lastLocation.time
                val fiveMinutesInMillis = 5 * 60 * 1000

                // 5분 이내의 최근 위치면 사용
                if (locationAge < fiveMinutesInMillis) {
                    return Result.success(lastLocation)
                }
            }

            // 2. 오래된 위치이거나 없으면 현재 위치 요청 (타임아웃 5초)
            val result = withTimeoutOrNull(3000) {
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_LOW_POWER,
                    null
                ).await()
            }
            if (result != null) {
                Result.success(result)
            } else {
                Result.failure(Exception("위치 정보를 가져올 수 없습니다."))
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
