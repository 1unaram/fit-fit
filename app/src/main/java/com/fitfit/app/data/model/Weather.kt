package com.fitfit.app.data.model

import com.fitfit.app.data.local.entity.WeatherEntity

data class Weather(
    val wid: String = "",
    val datetime: Long = 0,
    val description: String = "",
    val temperatureAvg: Double = 0.0,
    val temperatureMin: Double = 0.0,
    val temperatureMax: Double = 0.0,
    val precipitation: Double = 0.0,
    val windSpeed: Double = 0.0,
    val iconCode: String = "",
    val createdAt: Long = 0,
    var lastModified: Long = 0
) {
    constructor() : this(
        wid = "",
        datetime = 0,
        description = "",
        temperatureAvg = 0.0,
        temperatureMin = 0.0,
        temperatureMax = 0.0,
        precipitation = 0.0,
        windSpeed = 0.0,
        iconCode = "",
        createdAt = 0,
        lastModified = 0
    )

    companion object {
        fun fromEntity(entity: WeatherEntity): Weather {
            return Weather(
                wid = entity.wid,
                datetime = entity.datetime,
                description = entity.description,
                temperatureAvg = entity.temperatureAvg,
                temperatureMin = entity.temperatureMin,
                temperatureMax = entity.temperatureMax,
                precipitation = entity.precipitation,
                windSpeed = entity.windSpeed,
                iconCode = entity.iconCode,
                createdAt = entity.createdAt,
                lastModified = entity.lastModified
            )
        }
    }
}