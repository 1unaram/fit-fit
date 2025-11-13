package com.fitfit.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fitfit.app.data.local.dao.WeatherDao
import com.fitfit.app.data.local.entity.WeatherEntity
import com.fitfit.app.data.local.userPrefsDataStore
import com.fitfit.app.data.model.Weather
import com.fitfit.app.data.util.IdGenerator
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherRepository(
    private val weatherDao: WeatherDao,
    private val context: Context
) {
    private val firebaseWeatherRef = FirebaseDatabase.getInstance().reference.child("weathers")
    private val idGenerator = IdGenerator(context)

    private suspend fun getCurrentUid(): String? {
        return context.userPrefsDataStore.data.map {
            it[stringPreferencesKey("current_uid")]
        }.first()
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    suspend fun getWeatherByCurrentUser(): Flow<List<WeatherEntity>>? {
        return try {
            val uid = getCurrentUid() ?: return null
            weatherDao.getWeatherByUser(uid)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 날씨 정보 저장/업데이트
     */
    suspend fun insertWeather(
        datetime: Long,
        description: String,
        temperatureAvg: Double,
        temperatureMin: Double,
        temperatureMax: Double,
        precipitation: Double,
        windSpeed: Double,
        iconCode: String
    ): Result<String> {
        return try {
            val currentUid = getCurrentUid()
                ?: return Result.failure(Exception("로그인이 필요합니다."))

            val wid = idGenerator.generateNextWeatherId()

            val weather = WeatherEntity(
                wid = wid,
                ownerUid = currentUid,
                datetime = datetime,
                description = description,
                temperatureAvg = temperatureAvg,
                temperatureMin = temperatureMin,
                temperatureMax = temperatureMax,
                precipitation = precipitation,
                windSpeed = windSpeed,
                iconCode = iconCode,
                isSynced = false
            )

            // 1. Room에 저장
            weatherDao.insertWeather(weather)

            // 2. Firebase에 동기화
            syncToFirebase(weather)

            Result.success(wid)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * 날씨 정보 수정
     */
    suspend fun updateWeather(weather: WeatherEntity): Result<Unit> {
        return try {
            val updatedWeather = weather.copy(
                isSynced = false,
                lastModified = System.currentTimeMillis()
            )

            weatherDao.updateWeather(updatedWeather)
            syncToFirebase(updatedWeather)

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * 날씨 정보 삭제
     */
    suspend fun deleteWeather(wid: String): Result<Unit> {
        return try {
            val weather = weatherDao.getWeatherByWid(wid)
                ?: return Result.failure(Exception("날씨 정보를 찾을 수 없습니다."))

            // 1. Room에서 삭제
            weatherDao.deleteWeatherByWid(wid)

            // 2. Firebase에서 삭제
            firebaseWeatherRef
                .child(weather.ownerUid)
                .child(wid)
                .removeValue()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }


    /**
     * Firebase로 업로드
     */
    private suspend fun syncToFirebase(weather: WeatherEntity) {
        try {
            val firebaseRef = firebaseWeatherRef
                .child(weather.ownerUid)
                .child(weather.wid)

            val weatherData = Weather.fromEntity(weather)
            firebaseRef.setValue(weatherData).await()

            weatherDao.markAsSynced(weather.wid)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 동기화되지 않은 데이터 재동기화
     */
    suspend fun syncUnsyncedData() {
        val currentUid = getCurrentUid() ?: return
        val unsyncedWeather = weatherDao.getUnsyncedWeather(currentUid)

        unsyncedWeather.forEach { weather ->
            syncToFirebase(weather)
        }
    }

    /**
     * Firebase 실시간 동기화
     */
    fun startRealtimeSync(uid: String) {
        val firebaseUserWeatherRef = firebaseWeatherRef.child(uid)

        firebaseUserWeatherRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                syncWeatherFromFirebase(snapshot, uid)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                syncWeatherFromFirebase(snapshot, uid)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val wid = snapshot.key ?: return
                CoroutineScope(Dispatchers.IO).launch {
                    weatherDao.deleteWeatherByWid(wid)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun syncWeatherFromFirebase(snapshot: DataSnapshot, uid: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val wid = snapshot.child("wid").value as? String ?: return@launch
                val datetime = snapshot.child("datetime").value as? Long ?: 0L
                val description = snapshot.child("description").value as? String ?: ""
                val temperatureAvg = snapshot.child("temperatureAvg").value as? Double ?: 0.0
                val temperatureMin = snapshot.child("temperatureMin").value as? Double ?: 0.0
                val temperatureMax = snapshot.child("temperatureMax").value as? Double ?: 0.0
                val precipitation = snapshot.child("precipitation").value as? Double ?: 0.0
                val windSpeed = snapshot.child("windSpeed").value as? Double ?: 0.0
                val iconCode = snapshot.child("iconCode").value as? String ?: ""
                val createdAt = snapshot.child("createdAt").value as? Long ?: 0L
                val lastModified = snapshot.child("lastModified").value as? Long ?: 0L

                val weather = WeatherEntity(
                    wid = wid,
                    ownerUid = uid,
                    datetime = datetime,
                    description = description,
                    temperatureAvg = temperatureAvg,
                    temperatureMin = temperatureMin,
                    temperatureMax = temperatureMax,
                    precipitation = precipitation,
                    windSpeed = windSpeed,
                    iconCode = iconCode,
                    createdAt = createdAt,
                    isSynced = false,
                    lastModified = lastModified
                )

                weatherDao.insertWeather(weather)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
