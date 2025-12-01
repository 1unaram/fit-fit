package com.fitfit.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fitfit.app.data.local.dao.OutfitClothesDao
import com.fitfit.app.data.local.dao.OutfitDao
import com.fitfit.app.data.local.entity.OutfitClothesCrossRef
import com.fitfit.app.data.local.entity.OutfitEntity
import com.fitfit.app.data.local.entity.OutfitWithClothes
import com.fitfit.app.data.local.userPrefsDataStore
import com.fitfit.app.data.model.Outfit
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

class OutfitRepository(
    private val outfitDao: OutfitDao,
    private val outfitClothesDao: OutfitClothesDao,
    private val context: Context
) {

    private val firebaseOutfitsRef = FirebaseDatabase.getInstance().reference.child("outfits")
    private val idGenerator = IdGenerator(context)

    private suspend fun getCurrentUid(): String? {
        return context.userPrefsDataStore.data.map {
            it[stringPreferencesKey("current_uid")]
        }.first()
    }

    // 현재 사용자의 코디 목록 가져오기
    suspend fun getOutfitsByCurrentUser(): Flow<List<OutfitEntity>>? {
        return try {
            val uid = getCurrentUid() ?: return null
            outfitDao.getOutfitsByUser(uid)
        } catch (e: Exception) {
            null
        }
    }

    // 현재 사용자의 코디와 옷 정보 함께 가져오기
    suspend fun getOutfitsWithClothesByCurrentUser(): Flow<List<OutfitWithClothes>>? {
        return try {
            val uid = getCurrentUid() ?: return null
            outfitDao.getOutfitsWithClothesByUser(uid)
        } catch (e: Exception) {
            null
        }
    }

    // 코디 생성 (날씨 정보 없이)
    suspend fun createOutfit(
        clothesIds: List<String>,
        occasion: List<String>,
        comment: String?,
        wornStartTime: Long,
        wornEndTime: Long,
        latitude: Double,
        longitude: Double
    ): Result<String> {
        return try {

            val currentUid = getCurrentUid()
                ?: return Result.failure(Exception("Login required"))

            val oid = idGenerator.generateNextOutfitId()

            // 1. Outfit 생성
            val outfit = OutfitEntity(
                oid = oid,
                ownerUid = currentUid,
                clothesIds = clothesIds,
                occasion = occasion,
                comment = comment,
                wornStartTime = wornStartTime,
                wornEndTime = wornEndTime,
                latitude = latitude,
                longitude = longitude,
                weatherFetched = false,
                isSynced = false
            )

            // 2. Room에 저장
            outfitDao.insertOutfit(outfit)

            // 3. CrossRef 저장
            clothesIds.forEach { cid ->
                outfitClothesDao.insertCrossRef(
                    OutfitClothesCrossRef(oid = oid, cid = cid)
                )
            }

            // 4. Firebase에 동기화
            syncToFirebase(outfit)

            Result.success(oid)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // Outfit의 날씨 정보만 업데이트
    suspend fun updateOutfitWeather(
        oid: String,
        temperatureAvg: Double,
        temperatureMin: Double,
        temperatureMax: Double,
        description: String,
        iconCode: String,
        windSpeed: Double,
        precipitation: Double
    ): Result<Unit> {
        return try {
            val outfit = outfitDao.getOutfitById(oid)
                ?: return Result.failure(Exception("Outfit not found"))

            val updatedOutfit = outfit.copy(
                temperatureAvg = temperatureAvg,
                temperatureMin = temperatureMin,
                temperatureMax = temperatureMax,
                description = description,
                iconCode = iconCode,
                windSpeed = windSpeed,
                precipitation = precipitation,
                weatherFetched = true,
                isSynced = false,
                lastModified = System.currentTimeMillis()
            )

            // 1. Outfit 업데이트
            outfitDao.updateOutfit(updatedOutfit)

            // 2. Firebase 동기화
            syncToFirebase(updatedOutfit)

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // 날씨 미조회 Outfit 목록 조회
    suspend fun getOutfitsWithUnfetchedWeather(): List<OutfitEntity> {
        val currentUid = getCurrentUid() ?: return emptyList()
        return outfitDao.getOutfitsWithUnfetchedWeather(currentUid)
    }

    // 날씨 업데이트 대기 중인 Outfit 목록 조회
    suspend fun getPendingWeatherOutfits(): List<OutfitEntity> {
        val currentTime = System.currentTimeMillis()
        return outfitDao.getPendingWeatherOutfits(currentTime)
    }

    // Outfit 수정
    suspend fun updateOutfit(
        oid: String,
        clothesIds: List<String>
    ): Result<Unit> {
        return try {
            val outfit = outfitDao.getOutfitById(oid)
                ?: return Result.failure(Exception("Outfit not found"))

            val updatedOutfit = outfit.copy(
                clothesIds = clothesIds,
                isSynced = false,
                lastModified = System.currentTimeMillis()
            )

            // 1. Outfit 업데이트
            outfitDao.updateOutfit(updatedOutfit)

            // 2. CrossRef 재구성
            outfitClothesDao.deleteCrossRefsByOutfit(oid)
            clothesIds.forEach { cid ->
                outfitClothesDao.insertCrossRef(
                    OutfitClothesCrossRef(oid = oid, cid = cid)
                )
            }

            // 3. Firebase 동기화
            syncToFirebase(updatedOutfit)

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // Outfit 삭제
    suspend fun deleteOutfit(oid: String): Result<Unit> {
        return try {
            val outfit = outfitDao.getOutfitById(oid)
                ?: return Result.failure(Exception("Outfit not found"))

            // 1. CrossRef 삭제
            outfitClothesDao.deleteCrossRefsByOutfit(oid)

            // 2. Room에서 삭제
            outfitDao.deleteOutfitById(oid)

            // 3. Firebase에서 삭제
            firebaseOutfitsRef
                .child(outfit.ownerUid)
                .child(oid)
                .removeValue()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // Firebase로 동기화
    private suspend fun syncToFirebase(outfit: OutfitEntity) {
        try {
            val firebaseRef = firebaseOutfitsRef
                .child(outfit.ownerUid)
                .child(outfit.oid)

            val outfitData = Outfit.fromEntity(outfit)
            firebaseRef.setValue(outfitData).await()

            outfitDao.markAsSynced(outfit.oid)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 미동기화 데이터 동기화
    suspend fun syncUnsyncedData() {
        val currentUid = getCurrentUid() ?: return
        val unsyncedOutfits = outfitDao.getUnsyncedOutfits(currentUid)

        unsyncedOutfits.forEach { outfit ->
            syncToFirebase(outfit)
        }
    }

    // 실시간 동기화 시작
    fun startRealtimeSync(uid: String) {
        val firebaseUserOutfitsRef = firebaseOutfitsRef.child(uid)

        firebaseUserOutfitsRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                syncOutfitFromFirebase(snapshot, uid)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                syncOutfitFromFirebase(snapshot, uid)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val oid = snapshot.key ?: return
                CoroutineScope(Dispatchers.IO).launch {
                    outfitClothesDao.deleteCrossRefsByOutfit(oid)
                    outfitDao.deleteOutfitById(oid)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Firebase에서 Outfit 동기화
    private fun syncOutfitFromFirebase(snapshot: DataSnapshot, uid: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val oid = snapshot.child("oid").value as? String ?: return@launch
                val clothesIds = (snapshot.child("clothesIds").value as? List<*>)
                    ?.mapNotNull { it as? String } ?: emptyList()
                val occasion = (snapshot.child("occasion").value as? List<*>)
                    ?.mapNotNull { it as? String } ?: emptyList()
                val comment = snapshot.child("comment").value as? String ?: ""
                val wornStartTime = snapshot.child("wornStartTime").value as? Long ?: 0L
                val wornEndTime = snapshot.child("wornEndTime").value as? Long ?: 0L
                val latitude = snapshot.child("latitude").value as? Double ?: 0.0
                val longitude = snapshot.child("longitude").value as? Double ?: 0.0
                val description = snapshot.child("description").value as? String ?: ""
                val temperatureAvg = snapshot.child("temperatureAvg").value as? Double ?: 0.0
                val temperatureMin = snapshot.child("temperatureMin").value as? Double ?: 0.0
                val temperatureMax = snapshot.child("temperatureMax").value as? Double ?: 0.0
                val iconCode = snapshot.child("iconCode").value as? String ?: ""
                val windSpeed = snapshot.child("windSpeed").value as? Double ?: 0.0
                val precipitation = snapshot.child("precipitation").value as? Double ?: 0.0
                val weatherFetched = snapshot.child("weatherFetched").value as? Boolean ?: false
                val createdAt = snapshot.child("createdAt").value as? Long ?: 0L
                val lastModified = snapshot.child("lastModified").value as? Long ?: 0L

                val outfit = OutfitEntity(
                    oid = oid,
                    ownerUid = uid,
                    clothesIds = clothesIds,
                    occasion = occasion,
                    comment = comment,
                    wornStartTime = wornStartTime,
                    wornEndTime = wornEndTime,
                    latitude = latitude,
                    longitude = longitude,
                    description = description,
                    temperatureAvg = temperatureAvg,
                    temperatureMin = temperatureMin,
                    temperatureMax = temperatureMax,
                    iconCode = iconCode,
                    windSpeed = windSpeed,
                    precipitation = precipitation,
                    weatherFetched = weatherFetched,
                    createdAt = createdAt,
                    isSynced = true,
                    lastModified = lastModified
                )

                outfitDao.insertOutfit(outfit)

                outfitClothesDao.deleteCrossRefsByOutfit(oid)
                clothesIds.forEach { cid ->
                    outfitClothesDao.insertCrossRef(
                        OutfitClothesCrossRef(oid = oid, cid = cid)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}