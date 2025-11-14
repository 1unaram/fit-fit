package com.fitfit.app.data.remote.model

import com.google.gson.annotations.SerializedName

// One Call API 3.0 메인 응답
data class OneCallWeatherResponse(
    @SerializedName("lat")
    val lat: Double,

    @SerializedName("lon")
    val lon: Double,

    @SerializedName("timezone")
    val timezone: String,

    @SerializedName("timezone_offset")
    val timezoneOffset: Int,

    @SerializedName("current")
    val current: CurrentWeather,

    @SerializedName("minutely")
    val minutely: List<MinutelyWeather>? = null,

    @SerializedName("hourly")
    val hourly: List<HourlyWeather>? = null,

    @SerializedName("daily")
    val daily: List<DailyWeather>? = null,

    @SerializedName("alerts")
    val alerts: List<WeatherAlert>? = null
)

// 현재 날씨
data class CurrentWeather(
    @SerializedName("dt")
    val dt: Long,

    @SerializedName("sunrise")
    val sunrise: Long? = null,

    @SerializedName("sunset")
    val sunset: Long? = null,

    @SerializedName("temp")
    val temp: Double,

    @SerializedName("feels_like")
    val feelsLike: Double,

    @SerializedName("pressure")
    val pressure: Int,

    @SerializedName("humidity")
    val humidity: Int,

    @SerializedName("dew_point")
    val dewPoint: Double,

    @SerializedName("uvi")
    val uvi: Double,

    @SerializedName("clouds")
    val clouds: Int,

    @SerializedName("visibility")
    val visibility: Int,

    @SerializedName("wind_speed")
    val windSpeed: Double,

    @SerializedName("wind_deg")
    val windDeg: Int,

    @SerializedName("wind_gust")
    val windGust: Double? = null,

    @SerializedName("weather")
    val weather: List<Weather>,

    @SerializedName("rain")
    val rain: Rain? = null,

    @SerializedName("snow")
    val snow: Snow? = null
)

// 분 단위 예보
data class MinutelyWeather(
    @SerializedName("dt")
    val dt: Long,

    @SerializedName("precipitation")
    val precipitation: Double
)

// 시간 단위 예보
data class HourlyWeather(
    @SerializedName("dt")
    val dt: Long,

    @SerializedName("temp")
    val temp: Double,

    @SerializedName("feels_like")
    val feelsLike: Double,

    @SerializedName("pressure")
    val pressure: Int,

    @SerializedName("humidity")
    val humidity: Int,

    @SerializedName("dew_point")
    val dewPoint: Double,

    @SerializedName("uvi")
    val uvi: Double,

    @SerializedName("clouds")
    val clouds: Int,

    @SerializedName("visibility")
    val visibility: Int,

    @SerializedName("wind_speed")
    val windSpeed: Double,

    @SerializedName("wind_deg")
    val windDeg: Int,

    @SerializedName("wind_gust")
    val windGust: Double? = null,

    @SerializedName("weather")
    val weather: List<Weather>,

    @SerializedName("pop")
    val pop: Double,

    @SerializedName("rain")
    val rain: Rain? = null,

    @SerializedName("snow")
    val snow: Snow? = null
)

// 일 단위 예보
data class DailyWeather(
    @SerializedName("dt")
    val dt: Long,

    @SerializedName("sunrise")
    val sunrise: Long? = null,

    @SerializedName("sunset")
    val sunset: Long? = null,

    @SerializedName("moonrise")
    val moonrise: Long,

    @SerializedName("moonset")
    val moonset: Long,

    @SerializedName("moon_phase")
    val moonPhase: Double,

    @SerializedName("summary")
    val summary: String? = null,

    @SerializedName("temp")
    val temp: DailyTemp,

    @SerializedName("feels_like")
    val feelsLike: DailyFeelsLike,

    @SerializedName("pressure")
    val pressure: Int,

    @SerializedName("humidity")
    val humidity: Int,

    @SerializedName("dew_point")
    val dewPoint: Double,

    @SerializedName("wind_speed")
    val windSpeed: Double,

    @SerializedName("wind_deg")
    val windDeg: Int,

    @SerializedName("wind_gust")
    val windGust: Double? = null,

    @SerializedName("weather")
    val weather: List<Weather>,

    @SerializedName("clouds")
    val clouds: Int,

    @SerializedName("pop")
    val pop: Double,

    @SerializedName("rain")
    val rain: Double? = null,

    @SerializedName("snow")
    val snow: Double? = null,

    @SerializedName("uvi")
    val uvi: Double
)

// 일별 온도
data class DailyTemp(
    @SerializedName("day")
    val day: Double,

    @SerializedName("min")
    val min: Double,

    @SerializedName("max")
    val max: Double,

    @SerializedName("night")
    val night: Double,

    @SerializedName("eve")
    val eve: Double,

    @SerializedName("morn")
    val morn: Double
)

// 일별 체감 온도
data class DailyFeelsLike(
    @SerializedName("day")
    val day: Double,

    @SerializedName("night")
    val night: Double,

    @SerializedName("eve")
    val eve: Double,

    @SerializedName("morn")
    val morn: Double
)

// 날씨 상태
data class Weather(
    @SerializedName("id")
    val id: Int,

    @SerializedName("main")
    val main: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("icon")
    val icon: String
)

// 비
data class Rain(
    @SerializedName("1h")
    val oneHour: Double? = null
)

// 눈
data class Snow(
    @SerializedName("1h")
    val oneHour: Double? = null
)

// 기상 경보
data class WeatherAlert(
    @SerializedName("sender_name")
    val senderName: String,

    @SerializedName("event")
    val event: String,

    @SerializedName("start")
    val start: Long,

    @SerializedName("end")
    val end: Long,

    @SerializedName("description")
    val description: String,

    @SerializedName("tags")
    val tags: List<String>? = null
)

// Open Weather Time Machine API 응답
data class TimeMachineWeatherResponse(
    @SerializedName("lat")
    val lat: Double,

    @SerializedName("lon")
    val lon: Double,

    @SerializedName("timezone")
    val timezone: String,

    @SerializedName("timezone_offset")
    val timezoneOffset: Int,

    @SerializedName("data")
    val data: List<TimeMachineData>
)

data class TimeMachineData(
    @SerializedName("dt")
    val dt: Long,

    @SerializedName("sunrise")
    val sunrise: Long? = null,

    @SerializedName("sunset")
    val sunset: Long? = null,

    @SerializedName("temp")
    val temp: Double,

    @SerializedName("feels_like")
    val feelsLike: Double,

    @SerializedName("pressure")
    val pressure: Int,

    @SerializedName("humidity")
    val humidity: Int,

    @SerializedName("dew_point")
    val dewPoint: Double,

    @SerializedName("clouds")
    val clouds: Int,

    @SerializedName("visibility")
    val visibility: Int? = null,

    @SerializedName("wind_speed")
    val windSpeed: Double,

    @SerializedName("wind_deg")
    val windDeg: Int,

    @SerializedName("wind_gust")
    val windGust: Double? = null,

    @SerializedName("weather")
    val weather: List<Weather>,

    @SerializedName("rain")
    val rain: Rain? = null,

    @SerializedName("snow")
    val snow: Snow? = null
)
