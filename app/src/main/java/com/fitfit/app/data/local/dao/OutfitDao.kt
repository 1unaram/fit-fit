package com.fitfit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.fitfit.app.data.local.entity.ClothesWithOutfit
import com.fitfit.app.data.local.entity.OutfitClothesCrossRef
import com.fitfit.app.data.local.entity.OutfitEntity
import com.fitfit.app.data.local.entity.OutfitWithClothes
import kotlinx.coroutines.flow.Flow

@Dao
interface OutfitDao {
    // Outfit 기본 CRUD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: OutfitEntity)

    @Update
    suspend fun updateOutfit(outfit: OutfitEntity)

    @Delete
    suspend fun deleteOutfit(outfit: OutfitEntity)

    @Query("SELECT * FROM outfits ORDER BY created_at DESC")
    fun getAllOutfits(): Flow<List<OutfitEntity>>

    // Outfit과 Clothes 관계 조회
    @Transaction
    @Query("SELECT * FROM outfits WHERE oid = :oid")
    suspend fun getOutfitWithClothes(oid: String): OutfitWithClothes?

    @Transaction
    @Query("SELECT * FROM outfits ORDER BY created_at DESC")
    fun getAllOutfitsWithClothes(): Flow<List<OutfitWithClothes>>

    // 중간 테이블 관리
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfitClothesCrossRef(crossRef: OutfitClothesCrossRef)

    @Delete
    suspend fun deleteOutfitClothesCrossRef(crossRef: OutfitClothesCrossRef)

    @Query("DELETE FROM outfit_clothes_cross_ref WHERE oid = :oid")
    suspend fun deleteAllClothesFromOutfit(oid: String)

    // Clothes가 포함된 Outfit 찾기
    @Transaction
    @Query("SELECT * FROM clothes WHERE cid = :cid")
    suspend fun getClothesWithOutfits(cid: String): ClothesWithOutfit?

    // Firebase
    @Query("SELECT * FROM outfits WHERE is_synced = 0")
    suspend fun getUnsyncedOutfits(): List<OutfitEntity>

    @Query("UPDATE outfits SET is_synced = 1 WHERE oid = :oid")
    suspend fun markAsSynced(oid: String)

    // 모든 데이터 삭제
    @Query("DELETE FROM outfits")
    suspend fun deleteAllOutfits()

    @Query("DELETE FROM outfit_clothes_cross_ref")
    suspend fun deleteAllCrossRefs()
}