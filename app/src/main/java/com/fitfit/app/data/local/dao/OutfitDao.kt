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
    @Query("SELECT * FROM outfits WHERE ownerUid = :uid ORDER BY createdAt DESC")
    fun getOutfitsByUser(uid: String): Flow<List<OutfitEntity>>

    @Query("SELECT * FROM outfits WHERE oid = :oid")
    suspend fun getOutfitById(oid: String): OutfitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: OutfitEntity)

    @Update
    suspend fun updateOutfit(outfit: OutfitEntity)

    @Delete
    suspend fun deleteOutfit(outfit: OutfitEntity)

    @Query("DELETE FROM outfits WHERE oid = :oid")
    suspend fun deleteOutfitById(oid: String)

    @Transaction
    @Query("SELECT * FROM outfits WHERE ownerUid = :uid ORDER BY createdAt DESC")
    fun getOutfitsWithClothesByUser(uid: String): Flow<List<OutfitWithClothes>>

    @Transaction
    @Query("SELECT * FROM outfits WHERE oid = :oid")
    suspend fun getOutfitWithClothes(oid: String): OutfitWithClothes?

    @Query("UPDATE outfits SET isSynced = 1 WHERE oid = :oid")
    suspend fun markAsSynced(oid: String)

    @Query("SELECT * FROM outfits WHERE ownerUid = :uid AND isSynced = 0")
    suspend fun getUnsyncedOutfits(uid: String): List<OutfitEntity>

    @Query("DELETE FROM outfits WHERE ownerUid = :uid")
    suspend fun deleteAllOutfitsByUser(uid: String)
}