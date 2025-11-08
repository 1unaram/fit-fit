package com.fitfit.app.data.repository

import android.content.Context
import com.fitfit.app.data.local.dao.OutfitDao
import com.fitfit.app.data.local.entity.OutfitClothesCrossRef
import com.fitfit.app.data.local.entity.OutfitEntity
import com.fitfit.app.data.local.entity.OutfitWithClothes
import com.fitfit.app.data.model.Outfit
import com.fitfit.app.data.util.IdGenerator
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class OutfitRepository(
    private val outfitDao: OutfitDao,
    private val context: Context
) {

    private val firebaseDb = FirebaseDatabase.getInstance().reference.child("outfits")
    private val idGenerator = IdGenerator(context)

    fun getAllOutfitsWithClothes(): Flow<List<OutfitWithClothes>> {
        return outfitDao.getAllOutfitsWithClothes()
    }

    suspend fun getOutfitWithClothes(oid: String): OutfitWithClothes? {
        return outfitDao.getOutfitWithClothes(oid)
    }

    // Outfit 생성 및 옷 연결
    suspend fun createOutfitWithClothes(name: String, clothesIds: List<String>) {
        // 1. Outfit 생성
        val oid = idGenerator.generateNextOutfitId()
        val outfit = OutfitEntity(oid = oid, name = name)

        outfitDao.insertOutfit(outfit)

        // 2. 각 옷과 연결
        clothesIds.forEach { cid ->
            outfitDao.insertOutfitClothesCrossRef(
                OutfitClothesCrossRef(oid = oid, cid = cid)
            )
        }

        // 3. Firebase 동기화
        syncToFirebase(oid, clothesIds)
    }

    // Outfit에 옷 추가
    suspend fun addClothesToOutfit(oid: String, cid: String) {
        outfitDao.insertOutfitClothesCrossRef(
            OutfitClothesCrossRef(oid = oid, cid = cid)
        )
    }

    // Outfit에서 옷 제거
    suspend fun removeClothesFromOutfit(oid: String, cid: String) {
        outfitDao.deleteOutfitClothesCrossRef(
            OutfitClothesCrossRef(oid = oid, cid = cid)
        )
    }

    // Outfit 업데이트
    suspend fun updateOutfit(outfit: OutfitEntity) {
        outfitDao.updateOutfit(outfit.copy(isSynced = false))

        // Firebase 동기화
        val outfitWithClothes = outfitDao.getOutfitWithClothes(outfit.oid)
        if (outfitWithClothes != null) {
            val clothesIds = outfitWithClothes.clothes.map { it.cid }
            syncToFirebase(outfit.oid, clothesIds)
        }
    }

    // Outfit 삭제 (관련 관계도 자동 삭제됨)
    suspend fun deleteOutfit(outfit: OutfitEntity) {
        outfitDao.deleteAllClothesFromOutfit(outfit.oid)
        outfitDao.deleteOutfit(outfit)
    }

    // Firebase 동기화
    private suspend fun syncToFirebase(oid: String, clothesIds: List<String>) {
        try {
            val outfitWithClothes = outfitDao.getOutfitWithClothes(oid) ?: return
            val firebaseOutfit = Outfit.fromEntity(
                outfitWithClothes.outfit,
                clothesIds
            )

            firebaseDb.child(oid).setValue(firebaseOutfit).await()

            // 동기화 상태 업데이트
            val updatedOutfit = outfitWithClothes.outfit.copy(isSynced = true)
            outfitDao.updateOutfit(updatedOutfit)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 모든 Outfit 및 관계 삭제
    suspend fun deleteAllOutfits() {
        outfitDao.deleteAllCrossRefs()
        outfitDao.deleteAllOutfits()
    }

    // Firebase에서 삭제
    private suspend fun deleteFromFirebase(oid: String) {
        try {
            firebaseDb.child(oid).removeValue().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 동기화되지 않은 Outfit 수동 동기화
    suspend fun syncUnsyncedData() {
        // 구현 필요 시 추가
    }
}