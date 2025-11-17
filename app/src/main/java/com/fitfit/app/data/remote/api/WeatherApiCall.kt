package com.fitfit.app.data.remote.api

import com.fitfit.app.data.remote.model.OneCallWeatherResponse
import com.fitfit.app.data.remote.model.TimeMachineWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class WeatherApiCall {
    private val apiService = RetrofitInstance.weatherApiService

    suspend fun fetchOneCallWeather(
        latitude: Double,
        longitude: Double,
        apiKey: String,
        exclude: String? = null
    ): Result<OneCallWeatherResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getOneCallWeather(
                lat = latitude,
                lon = longitude,
                appid = apiKey,
                exclude = exclude,
                units = "metric"
            )
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchTimemachineWeather(
        latitude: Double,
        longitude: Double,
        timestamp: Long,
        apiKey: String
    ): Result<TimeMachineWeatherResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTimemachineWeather(
                lat = latitude,
                lon = longitude,
                timestamp = timestamp,
                appid = apiKey,
                units = "metric"
            )
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): Result<List<OWGeoResult>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.reverseGeocode(
                lat = latitude,
                lon = longitude,
                limit = 1,
                apiKey = apiKey
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun <T> handleResponse(response: Response<T>): Result<T> {
        return if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else {
            Result.failure(
                Exception("API 오류: ${response.code()} - ${response.message()}")
            )
        }
    }
}
