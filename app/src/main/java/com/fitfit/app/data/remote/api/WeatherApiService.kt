package com.fitfit.app.data.remote.api

import com.fitfit.app.data.remote.model.OneCallWeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    /**
     * One Call API 3.0 - 현재 날씨 및 예보 조회
     *
     * @param lat 위도 (-90 ~ 90)
     * @param lon 경도 (-180 ~ 180)
     * @param appid API 키
     * @param exclude 제외할 데이터 (예: "minutely,hourly,daily,alerts")
     * @param units 단위 (standard, metric, imperial) - 기본값: metric (섭씨)
     * @param lang 언어 (kr: 한국어)
     */
    @GET("data/3.0/onecall")
    suspend fun getOneCallWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
        @Query("exclude") exclude: String? = null,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "kr"
    ): Response<OneCallWeatherResponse>
}