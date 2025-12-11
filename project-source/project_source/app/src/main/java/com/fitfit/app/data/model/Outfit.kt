package com.fitfit.app.data.model

import com.fitfit.app.data.local.entity.OutfitEntity

data class Outfit(
    val oid: String = "",
    val clothesIds: List<String> = emptyList(),

    // 착용 정보
    val wornStartTime: Long = 0,
    val wornEndTime: Long = 0,
    val occasion: List<String> = emptyList(),
    val comment: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,

    // 날씨 정보
    val temperatureAvg: Double? = null,
    val temperatureMin: Double? = null,
    val temperatureMax: Double? = null,
    val description: String? = null,
    val iconCode: String? = null,
    val windSpeed: Double? = null,
    val precipitation: Double? = null,
    val weatherFetched: Boolean = false,

    val createdAt: Long = 0,
    val lastModified: Long = 0
) {
    constructor() : this(
        "",
        emptyList(),
        0,
        0,
        emptyList(),
        null,
        0.0,
        0.0,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        false,
        0,
        0
    )

    companion object {
        fun fromEntity(entity: OutfitEntity): Outfit {
            return Outfit(
                oid = entity.oid,
                clothesIds = entity.clothesIds,
                occasion = entity.occasion,
                comment = entity.comment,
                wornStartTime = entity.wornStartTime,
                wornEndTime = entity.wornEndTime,
                latitude = entity.latitude,
                longitude = entity.longitude,
                temperatureAvg = entity.temperatureAvg,
                temperatureMin = entity.temperatureMin,
                temperatureMax = entity.temperatureMax,
                description = entity.description,
                iconCode = entity.iconCode,
                windSpeed = entity.windSpeed,
                precipitation = entity.precipitation,
                weatherFetched = entity.weatherFetched,
                createdAt = entity.createdAt,
                lastModified = entity.lastModified
            )
        }
    }
}