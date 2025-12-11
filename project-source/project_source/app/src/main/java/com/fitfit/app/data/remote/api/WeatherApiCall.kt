package com.fitfit.app.data.remote.api

import com.fitfit.app.data.remote.model.OneCallWeatherResponse
import com.fitfit.app.data.remote.model.TimeMachineWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class WeatherApiCall {
    private val apiService = RetrofitInstance.weatherApiService

    // One Call API 3.0 - 현재 날씨 및 예보 조회
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

    // One Call API 3.0 - 과거 날씨 조회 (타임머신)
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

    // 위도경도 값으로 주소 변환 (역지오코딩)
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
                Exception("API call failed..")
            )
        }
    }
}
