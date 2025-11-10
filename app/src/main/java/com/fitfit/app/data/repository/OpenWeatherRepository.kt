package com.fitfit.app.data.repository

import com.fitfit.app.data.remote.api.WeatherApiCall
import com.fitfit.app.data.remote.model.OneCallWeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OpenWeatherRepository(
    private val apiKey: String
) {
    private val weatherApiCall = WeatherApiCall()

    /**
     * One Call API 3.0으로 전체 날씨 정보 가져오기
     * (현재 날씨, 분/시간/일별 예보 모두 포함)
     */
    fun getOneCallWeather(
        latitude: Double,
        longitude: Double
    ): Flow<Result<OneCallWeatherResponse>> = flow {
        emit(weatherApiCall.fetchOneCallWeather(latitude, longitude, apiKey))
    }

    /**
     * 현재 날씨와 일별 예보만 가져오기 (minutely, hourly, alerts 제외)
     */
    fun getCurrentAndDailyWeather(
        latitude: Double,
        longitude: Double
    ): Flow<Result<OneCallWeatherResponse>> = flow {
        emit(
            weatherApiCall.fetchOneCallWeather(
                latitude,
                longitude,
                apiKey,
                exclude = "minutely,hourly,alerts"
            )
        )
    }

    /**
     * 현재 날씨만 가져오기
     */
    fun getCurrentWeatherOnly(
        latitude: Double,
        longitude: Double
    ): Flow<Result<OneCallWeatherResponse>> = flow {
        emit(
            weatherApiCall.fetchOneCallWeather(
                latitude,
                longitude,
                apiKey,
                exclude = "minutely,hourly,daily,alerts"
            )
        )
    }

    fun getTimemachineWeather(
        latitude: Double,
        longitude: Double,
        timestamp: Long
    ): Flow<Result<OneCallWeatherResponse>> = flow {
        emit(
            weatherApiCall.fetchTimemachineWeather(
                latitude,
                longitude,
                timestamp,
                apiKey
            )
        )
    }
}
