package com.fitfit.app.data.remote.api

import com.fitfit.app.data.remote.model.OneCallWeatherResponse
import com.fitfit.app.data.remote.model.TimeMachineWeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    // One Call API 3.0 - 현재 날씨 및 예보 조회
    @GET("data/3.0/onecall")
    suspend fun getOneCallWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
        @Query("exclude") exclude: String? = null,
        @Query("units") units: String = "metric"
    ): Response<OneCallWeatherResponse>

    // One Call API 3.0 - 과거 날씨 조회 (타임머신)
    @GET("data/3.0/onecall/timemachine")
    suspend fun getTimemachineWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("dt") timestamp: Long,
        @Query("appid") appid: String,
        @Query("units") units: String = "metric"
    ): Response<TimeMachineWeatherResponse>

    // 위도경도 값으로 주소 변환 (역지오코딩)
    @GET("geo/1.0/reverse")
    suspend fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String
    ): List<OWGeoResult>

}

data class OWGeoResult(
    val name: String,
    val localNames: Map<String, String>?,
    val country: String,
    val state: String?
)