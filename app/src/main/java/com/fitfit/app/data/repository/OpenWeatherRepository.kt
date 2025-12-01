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


    // Case1: 현재 날씨 가져오기 - WeatherCard
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

    // Case2: 과거 날씨 가져오기
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

    // Case3: 미래 날씨 가져오기 - WeatherFilter
    fun getForecastWeather(
        latitude: Double,
        longitude: Double
    ): Flow<Result<OneCallWeatherResponse>> = flow {
        emit(
            weatherApiCall.fetchOneCallWeather(
                latitude,
                longitude,
                apiKey,
                exclude = "current,minutely,alerts,hourly"
            )
        )
    }

    // Case4: 오늘, 일주일 날씨 가져오기 - WeatherScreen
    fun getTodayAndWeeklyWeather(
        latitude: Double,
        longitude: Double
    ): Flow<Result<OneCallWeatherResponse>> = flow {
        emit(
            weatherApiCall.fetchOneCallWeather(
                latitude,
                longitude,
                apiKey,
                exclude = "minutely,alerts"
            )
        )
    }


    // Location Name 가져오기
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
