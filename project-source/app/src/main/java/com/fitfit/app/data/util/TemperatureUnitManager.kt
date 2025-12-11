package com.fitfit.app.data.util

import android.annotation.SuppressLint

object TemperatureUnitManager {
    fun celsiusToFahrenheit(celsius: Double): Double {
        return celsius * 9 / 5 + 32
    }

    @SuppressLint("DefaultLocale")
    fun formatTemperature(temp: Double?, isFahrenheit: Boolean): String {
        if (temp == null) return "N/A"
        val convertedTemp = if (isFahrenheit) celsiusToFahrenheit(temp) else temp
        val unit = if (isFahrenheit) "°F" else "°C"
        return String.format("%.1f%s", convertedTemp, unit)
    }
}