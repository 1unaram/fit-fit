package com.fitfit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.fitfit.app.data.local.entity.OutfitEntity
import com.fitfit.app.data.local.entity.OutfitWithClothes
import kotlinx.coroutines.flow.Flow

@Dao
interface OutfitDao {
    @Query("SELECT * FROM outfits WHERE ownerUid = :uid ORDER BY wornStartTime DESC")
    fun getOutfitsByUser(uid: String): Flow<List<OutfitEntity>>

    @Query("SELECT * FROM outfits WHERE oid = :oid")
    suspend fun getOutfitById(oid: String): OutfitEntity?

    @Transaction
    @Query("SELECT * FROM outfits WHERE ownerUid = :uid ORDER BY wornStartTime DESC")
    fun getOutfitsWithClothesByUser(uid: String): Flow<List<OutfitWithClothes>>

    @Transaction
    @Query("SELECT * FROM outfits WHERE oid = :oid")
    suspend fun getOutfitWithClothes(oid: String): OutfitWithClothes?

    // ========== 날씨 미조회 Outfit 조회 ==========
    @Query("SELECT * FROM outfits WHERE ownerUid = :uid AND weatherFetched = 0")
    suspend fun getOutfitsWithUnfetchedWeather(uid: String): List<OutfitEntity>

    // ========= 날씨 업데이트 대기 중인 Outfit 조회 ==========
    @Query("SELECT * FROM outfits WHERE weatherFetched = 0 AND wornEndTime < :currentTime")
    suspend fun getPendingWeatherOutfits(currentTime: Long): List<OutfitEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: OutfitEntity)

    @Update
    suspend fun updateOutfit(outfit: OutfitEntity)

    @Delete
    suspend fun deleteOutfit(outfit: OutfitEntity)

    @Query("DELETE FROM outfits WHERE oid = :oid")
    suspend fun deleteOutfitById(oid: String)


    @Query("UPDATE outfits SET isSynced = 1 WHERE oid = :oid")
    suspend fun markAsSynced(oid: String)

    @Query("SELECT * FROM outfits WHERE ownerUid = :uid AND isSynced = 0")
    suspend fun getUnsyncedOutfits(uid: String): List<OutfitEntity>

    @Query("DELETE FROM outfits WHERE ownerUid = :uid")
    suspend fun deleteAllOutfitsByUser(uid: String)
}