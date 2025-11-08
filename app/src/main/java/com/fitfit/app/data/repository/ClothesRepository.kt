package com.fitfit.app.data.repository

import android.content.Context
import com.fitfit.app.data.local.dao.ClothesDao
import com.fitfit.app.data.local.entity.ClothesEntity
import com.fitfit.app.data.model.Clothes
import com.fitfit.app.data.util.IdGenerator
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class ClothesRepository(
    private val clothesDao: ClothesDao,
    private val context: Context
) {

    private val firebaseDb = FirebaseDatabase.getInstance().reference.child("clothes")
    private val idGenerator = IdGenerator(context)

    // 모든 옷 가져오기
    fun getAllClothes(): Flow<List<ClothesEntity>> {
        return clothesDao.getAllClothes()
    }

    suspend fun insertClothes(name: String, category: String) {
        val cid = idGenerator.generateNextClothesId()
        val clothes = ClothesEntity(
            cid = cid,
            name = name,
            category = category
        )
        clothesDao.insertClothes(clothes)
        syncToFirebase(cid)
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