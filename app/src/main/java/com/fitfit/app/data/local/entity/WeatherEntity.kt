package com.fitfit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weathers")
data class WeatherEntity(
    @PrimaryKey
    val wid: String,

    val ownerUid: String,

    val datetime: Long,

    val description: String,

    val temperatureAvg: Double,

    val temperatureMin: Double,

    val temperatureMax: Double,

    val precipitation: Double,

    val windSpeed: Double,

    val iconCode: String = "",

    val createdAt: Long = System.currentTimeMillis(),

    var isSynced: Boolean = false,

    var lastModified: Long = System.currentTimeMillis()
)