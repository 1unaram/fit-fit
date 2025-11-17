package com.fitfit.app.data.util

import java.util.concurrent.TimeUnit

object WeatherAggregator {

    /**
     * 시작 시간부터 종료 시간까지 1시간 간격으로 타임스탬프 생성
     * @param startTime 시작 시간 (Unix timestamp millis)
     * @param endTime 종료 시간 (Unix timestamp millis)
     * @return 1시간 간격 타임스탬프 리스트
     */
    fun generateHourlyTimestamps(startTime: Long, endTime: Long): List<Long> {
        val timestamps = mutableListOf<Long>()
        var currentTime = startTime

        // 시작 시간 추가
        timestamps.add(currentTime)

        // 1시간(3600초) 간격으로 추가
        val oneHourInMillis = TimeUnit.HOURS.toMillis(1)

        while (currentTime < endTime) {
            currentTime += oneHourInMillis
            timestamps.add(currentTime)
        }

        return timestamps
    }

    /**
     * 여러 날씨 데이터를 집계
     */
    data class WeatherData(
        val temperature: Double,
        val weatherDescription: String,
        val weatherIcon: String,
        val windSpeed: Double,
        val precipitation: Double
    )

    data class AggregatedWeather(
        val temperatureAvg: Double,
        val temperatureMin: Double,
        val temperatureMax: Double,
        val windSpeedAvg: Double,
        val precipitationAvg: Double,
        val mostCommonDescription: String,
        val mostCommonIcon: String
    )

    /**
     * 날씨 데이터 집계
     */
    fun aggregateWeatherData(weatherDataList: List<WeatherData>): AggregatedWeather? {
        if (weatherDataList.isEmpty()) return null

        // 온도 집계
        val temperatures = weatherDataList.map { it.temperature }
        val temperatureAvg = temperatures.average()
        val temperatureMin = temperatures.minOrNull() ?: 0.0
        val temperatureMax = temperatures.maxOrNull() ?: 0.0

        // 풍속 평균
        val windSpeedAvg = weatherDataList.map { it.windSpeed }.average()

        // 강수량 평균
        val precipitationAvg = weatherDataList.map { it.precipitation }.average()

        // 가장 빈번한 날씨 상태
        val descriptionFrequency = weatherDataList
            .groupingBy { it.weatherDescription }
            .eachCount()
        val mostCommonDescription = descriptionFrequency
            .maxByOrNull { it.value }?.key ?: "알 수 없음"

        // 가장 빈번한 아이콘
        val iconFrequency = weatherDataList
            .groupingBy { it.weatherIcon }
            .eachCount()
        val mostCommonIcon = iconFrequency
            .maxByOrNull { it.value }?.key ?: ""

        return AggregatedWeather(
            temperatureAvg = temperatureAvg,
            temperatureMin = temperatureMin,
            temperatureMax = temperatureMax,
            windSpeedAvg = windSpeedAvg,
            precipitationAvg = precipitationAvg,
            mostCommonDescription = mostCommonDescription,
            mostCommonIcon = mostCommonIcon
        )
    }
}