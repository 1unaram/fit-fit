package com.fitfit.app.data.remote.api

import com.fitfit.app.data.remote.model.OneCallWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class WeatherApiCall {
    private val apiService = RetrofitInstance.weatherApiService

    /**
     * One Call API 3.0으로 날씨 데이터 가져오기
     *
     * @param latitude 위도
     * @param longitude 경도
     * @param apiKey API 키
     * @param exclude 제외할 데이터 부분 (쉼표로 구분, 예: "minutely,alerts")
     *                 가능한 값: current, minutely, hourly, daily, alerts
     * @return Result<OneCallWeatherResponse>
     */
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
                units = "metric", // 섭씨 사용
                lang = "kr" // 한국어 설명
            )
            handleResponse(response)
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
