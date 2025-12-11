package com.fitfit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitfit.app.data.local.entity.OutfitClothesCrossRef

@Dao
interface OutfitClothesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(crossRef: OutfitClothesCrossRef)

    @Delete
    suspend fun deleteCrossRef(crossRef: OutfitClothesCrossRef)

    @Query("DELETE FROM outfit_clothes_cross_ref WHERE oid = :oid")
    suspend fun deleteCrossRefsByOutfit(oid: String)

    @Query("DELETE FROM outfit_clothes_cross_ref WHERE cid = :cid")
    suspend fun deleteCrossRefsByClothes(cid: String)

    @Query("SELECT * FROM outfit_clothes_cross_ref WHERE oid = :oid")
    suspend fun getCrossRefsByOutfit(oid: String): List<OutfitClothesCrossRef>
}