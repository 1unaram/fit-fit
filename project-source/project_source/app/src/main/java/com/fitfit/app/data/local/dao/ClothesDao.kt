package com.fitfit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitfit.app.data.local.entity.ClothesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothesDao {

    @Query("SELECT * FROM clothes WHERE ownerUid = :uid ORDER BY createdAt DESC")
    fun getClothesByUser(uid: String): Flow<List<ClothesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClothes(clothes: ClothesEntity)

    @Update
    suspend fun updateClothes(clothes: ClothesEntity)

    @Query("DELETE FROM clothes WHERE cid = :cid")
    suspend fun deleteClothesById(cid: String)

    @Query("SELECT * FROM clothes WHERE cid = :cid")
    suspend fun getClothesById(cid: String): ClothesEntity?

    @Query("SELECT * FROM clothes WHERE ownerUid = :uid AND isSynced = 0")
    suspend fun getUnsyncedClothes(uid: String): List<ClothesEntity>

    @Query("UPDATE clothes SET isSynced = 1, lastModified = :timestamp WHERE cid = :cid")
    suspend fun markAsSynced(cid: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM clothes WHERE ownerUid = :uid")
    suspend fun deleteAllClothesByUser(uid: String)
}