package com.fitfit.app.data.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class LocationHelper(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Pair<Double, Double>? {
        if (!hasLocationPermission()) {
            return null
        }

        return try {
            // 1. 마지막 위치 시도 (5분 이내의 최근 위치만 사용)
            val lastLocation = fusedLocationClient.lastLocation.await()
            if (lastLocation != null) {
                val locationAge = System.currentTimeMillis() - lastLocation.time
                val fiveMinutesInMillis = 5 * 60 * 1000

                // 5분 이내의 최근 위치면 사용
                if (locationAge < fiveMinutesInMillis) {
                    return Pair(lastLocation.latitude, lastLocation.longitude)
                }
            }

            // 2. 오래된 위치이거나 없으면 현재 위치 요청 (타임아웃 5초)
            withTimeoutOrNull(5000) {
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_LOW_POWER,
                    CancellationTokenSource().token
                ).await()

                location?.let { Pair(it.latitude, it.longitude) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
}