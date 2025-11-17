package com.fitfit.app.data.repository

import com.fitfit.app.data.remote.api.OWGeoResult
import com.fitfit.app.data.remote.api.WeatherApiCall
import com.fitfit.app.data.remote.model.OneCallWeatherResponse
import com.fitfit.app.data.remote.model.TimeMachineWeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OpenWeatherRepository(
    private val apiKey: String
) {
    private val weatherApiCall = WeatherApiCall()


    // 현재 날씨 가져오기 for Case1: WeatherCard
    fun getCurrentWeather(
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

    // 과거 날씨 가져오기 for Case2: PastWeather
    fun getTimemachineWeather(
        latitude: Double,
        longitude: Double,
        timestamp: Long
    ): Flow<Result<TimeMachineWeatherResponse>> = flow {
        emit(
            weatherApiCall.fetchTimemachineWeather(
                latitude,
                longitude,
                timestamp,
                apiKey
            )
        )
    }

    fun getLocationName(
        latitude: Double,
        longitude: Double
    ): Flow<Result<List<OWGeoResult>>> = flow {
        emit(
            weatherApiCall.reverseGeocode(
                latitude,
                longitude,
                apiKey
            )
        )
    }
}
