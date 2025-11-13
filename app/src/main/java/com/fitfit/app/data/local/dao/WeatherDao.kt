package com.fitfit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitfit.app.data.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    // 특정 사용자의 모든 날씨 기록 조회
    @Query("SELECT * FROM weathers WHERE ownerUid = :uid ORDER BY datetime DESC")
    fun getWeatherByUser(uid: String): Flow<List<WeatherEntity>>

    // ID로 날씨 조회
    @Query("SELECT * FROM weathers WHERE wid = :wid")
    suspend fun getWeatherByWid(wid: String): WeatherEntity?

    // 날씨 정보 저장
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    // 날씨 정보 수정
    @Update
    suspend fun updateWeather(weather: WeatherEntity)

    // 날씨 정보 삭제
    @Query("DELETE FROM weathers WHERE wid = :wid")
    suspend fun deleteWeatherByWid(wid: String)

    // 동기화 완료 표시
    @Query("UPDATE weathers SET isSynced = 1 WHERE wid = :wid")
    suspend fun markAsSynced(wid: String)

    // 동기화되지 않은 날씨 정보 조회
    @Query("SELECT * FROM weathers WHERE ownerUid = :uid AND isSynced = 0")
    suspend fun getUnsyncedWeather(uid: String): List<WeatherEntity>
}