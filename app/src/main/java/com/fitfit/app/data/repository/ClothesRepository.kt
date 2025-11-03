package com.fitfit.app.data.repository

import com.fitfit.app.data.local.dao.ClothesDao
import com.fitfit.app.data.local.entity.ClothesEntity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class ClothesRepository(private val clothesDao: ClothesDao) {

    private val firebaseDb = FirebaseDatabase.getInstance().reference.child("clothes")

    // 모든 옷 가져오기
    fun getAllClothes(): Flow<List<ClothesEntity>> {
        return clothesDao.getAllClothes()
    }

    suspend fun insertClothes(clothes: ClothesEntity) {
        clothesDao.insertClothes(clothes)
        syncToFirebase(clothes.cid)  // cid 사용
    }

    private suspend fun syncToFirebase(cid: String) {
        try {
            val clothesEntity = clothesDao.getClothesById(cid) ?: return
            val firebaseClothes = Clothes.fromEntity(clothesEntity)

            // Firebase에 커스텀 키로 저장
            firebaseDb.child(cid).setValue(firebaseClothes).await()
            clothesDao.markAsSynced(cid)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}