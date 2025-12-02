package com.fitfit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.fitfit.app.data.local.converters.ListConverter

@Entity(tableName = "outfits")
@TypeConverters(ListConverter::class)
data class OutfitEntity(

    // ============ 기본 정보 ============
    @PrimaryKey
    val oid: String,
    val ownerUid: String,
    val clothesIds: List<String>,
    val occasion: List<String>,
    val comment: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    var isSynced: Boolean = false,
    var lastModified: Long = System.currentTimeMillis(),

    // ============ 착용 정보 ============
    val wornStartTime: Long,
    val wornEndTime: Long,
    val latitude: Double,
    val longitude: Double,

    // ============ 날씨 정보 (nullable) ============
    val temperatureAvg: Double? = null,
    val temperatureMin: Double? = null,
    val temperatureMax: Double? = null,
    val description: String? = null,
    val iconCode: String? = null,
    val windSpeed: Double? = null,
    val precipitation: Double? = null,

    // ========== 날씨 조회 상태 ==========
    val weatherFetched: Boolean = false,

    // ========== 날씨 업데이트 상태 ==========
    // FETCHED: 완료됨
    // UPDATING: API 계산 중 (곧 업데이트 예정)
    // PENDING: 미래 시간이라 기다려야 함
    val weatherUpdateStatus: String = "PENDING"
)

// 날씨 업데이트 상태 enum
enum class WeatherUpdateStatus {
    FETCHED,      // 날씨 조회 완료
    UPDATING,     // API 계산 중
    PENDING       // 미래 시간으로 기다리는 중
}
