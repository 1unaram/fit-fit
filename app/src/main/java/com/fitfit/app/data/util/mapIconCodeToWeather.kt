package com.fitfit.app.data.util

fun mapIconCodeToWeather(iconCode: String?): String? {
    return when (iconCode) {
        "01d", "01n" -> "Sunny"
        "02d", "02n", "03d", "03n", "04d", "04n" -> "Cloud"
        "09d", "09n", "10d", "10n" -> "Rain"
        "13d", "13n" -> "Snow"
        else -> null
    }
}