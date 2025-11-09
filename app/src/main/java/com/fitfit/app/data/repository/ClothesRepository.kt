package com.fitfit.app.data.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fitfit.app.data.local.dao.ClothesDao
import com.fitfit.app.data.local.entity.ClothesEntity
import com.fitfit.app.data.local.userPrefsDataStore
import com.fitfit.app.data.model.Clothes
import com.fitfit.app.data.util.IdGenerator
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ClothesRepository(
    private val clothesDao: ClothesDao,
    private val context: Context
) {
    private val firebaseClothesRef = FirebaseDatabase.getInstance().reference.child("clothes")
    private val idGenerator = IdGenerator(context)

    private suspend fun getCurrentUid(): String? {
        return context.userPrefsDataStore.data.map {
            it[stringPreferencesKey("current_uid")]
        }.first()
    }

    /**
     * 현재 사용자의 옷 목록 가져오기
     */
    suspend fun getClothesByCurrentUser(): Flow<List<ClothesEntity>>? {
        return try {
            val uid = getCurrentUid() ?: return null
            clothesDao.getClothesByUser(uid)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 옷 추가 (Room 먼저 → Firebase 동기화)
     */
    suspend fun insertClothes(
        name: String,
        category: String,
        brand: String = "",
        color: String = ""
    ): Result<String> {
        return try {
            val currentUid = getCurrentUid()
                ?: return Result.failure(Exception("로그인이 필요합니다."))

            val cid = idGenerator.generateNextClothesId()

            val clothes = ClothesEntity(
                cid = cid,
                ownerUid = currentUid,
                name = name,
                category = category,
                isSynced = false
            )

            // 1. Room에 저장
            clothesDao.insertClothes(clothes)

            // 2. Firebase에 동기화
            syncToFirebase(clothes)

            Result.success(cid)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * 옷 수정
     */
    suspend fun updateClothes(clothes: ClothesEntity): Result<Unit> {
        return try {
            val updatedClothes = clothes.copy(
                isSynced = false,
                lastModified = System.currentTimeMillis()
            )

            clothesDao.updateClothes(updatedClothes)
            syncToFirebase(updatedClothes)

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * 옷 삭제
     */
    suspend fun deleteClothes(cid: String): Result<Unit> {
        return try {
            val clothes = clothesDao.getClothesById(cid)
                ?: return Result.failure(Exception("옷을 찾을 수 없습니다."))

            // 1. Room에서 삭제
            clothesDao.deleteClothesById(cid)

            // 2. Firebase에서 삭제
            firebaseClothesRef
                .child(clothes.ownerUid)
                .child(cid)
                .removeValue()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Firebase로 업로드
     */
    private suspend fun syncToFirebase(clothes: ClothesEntity) {
        try {
            val firebaseRef = firebaseClothesRef
                .child(clothes.ownerUid)
                .child(clothes.cid)

            val clothesData = Clothes.fromEntity(clothes)
            firebaseRef.setValue(clothesData).await()

            clothesDao.markAsSynced(clothes.cid)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 동기화되지 않은 데이터 재동기화
     */
    suspend fun syncUnsyncedData() {
        val currentUid = getCurrentUid() ?: return
        val unsyncedClothes = clothesDao.getUnsyncedClothes(currentUid)

        unsyncedClothes.forEach { clothes ->
            syncToFirebase(clothes)
        }
    }

    /**
     * Firebase 실시간 동기화
     */
    fun startRealtimeSync(uid: String) {
        val firebaseUserClothesRef = firebaseClothesRef.child(uid)

        firebaseUserClothesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                syncClothesFromFirebase(snapshot, uid)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                syncClothesFromFirebase(snapshot, uid)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val cid = snapshot.key ?: return
                CoroutineScope(Dispatchers.IO).launch {
                    clothesDao.deleteClothesById(cid)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun syncClothesFromFirebase(snapshot: DataSnapshot, uid: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cid = snapshot.child("cid").value as? String ?: return@launch
                val name = snapshot.child("name").value as? String ?: ""
                val category = snapshot.child("category").value as? String ?: ""
                val createdAt = snapshot.child("createdAt").value as? Long ?: 0L
                val lastModified = snapshot.child("lastModified").value as? Long ?: 0L

                val clothes = ClothesEntity(
                    cid = cid,
                    ownerUid = uid,
                    name = name,
                    category = category,
                    createdAt = createdAt,
                    lastModified = lastModified,
                    isSynced = true
                )

                clothesDao.insertClothes(clothes)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}