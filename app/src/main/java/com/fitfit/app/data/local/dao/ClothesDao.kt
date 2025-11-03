package com.fitfit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.fitfit.app.data.local.entity.ClothesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothesDao {
    @Query("SELECT * FROM clothes ORDER BY created_at DESC")
    fun getAllClothes(): Flow<List<ClothesEntity>>

    @Query("SELECT * FROM clothes WHERE category = :category ORDER BY created_at DESC")
    fun getClothesByCategory(category: String): Flow<List<ClothesEntity>>

    @Query("SELECT * FROM clothes WHERE cid = :cid")
    suspend fun getClothesById(cid: String): ClothesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClothes(clothes: ClothesEntity)

    @Query("UPDATE clothes SET is_synced = 1 WHERE cid = :cid")
    suspend fun markAsSynced(cid: String)
}