package com.fitfit.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fitfit.app.data.local.dao.ClothesDao
import com.fitfit.app.data.local.entity.ClothesEntity
import com.fitfit.app.data.local.userPrefsDataStore
import com.fitfit.app.data.util.IdGenerator
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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

    // ### 현재 사용자의 옷 목록 불러오기 ###
    suspend fun getClothesByCurrentUser(): Flow<List<ClothesEntity>>? {
        return try {
            val uid = getCurrentUid() ?: return null
            clothesDao.getClothesByUser(uid)
        } catch (e: Exception) {
            null
        }
    }

    // ### 옷 추가 (RoomDB 저장 -> Firebase 동기화) ###
    suspend fun insertClothes(
        imagePath: String,
        category: String,
        nickname: String,
        storeUrl: String?
    ): Result<String> {
        return try {
            val currentUid = getCurrentUid()
                ?: return Result.failure(Exception("로그인이 필요합니다."))

            val cid = idGenerator.generateNextClothesId()

            val entity = ClothesEntity(
                cid = cid,
                ownerUid = currentUid,
                imagePath = imagePath,
                category = category,
                nickname = nickname,
                storeUrl = storeUrl,
                isSynced = false
            )

            clothesDao.insertClothes(entity)
            syncToFirebase(entity)
            Result.success(cid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ### 옷 수정 ###
    suspend fun updateClothes(
        cid: String,
        category: String,
        nickname: String,
        storeUrl: String?
    ): Result<Unit> {
        return try {
            val currentUid = getCurrentUid()
                ?: return Result.failure(Exception("로그인이 필요합니다."))

            val entity = clothesDao.getClothesById(cid)
                ?: return Result.failure(Exception("Failed to find the clothes."))

            val updated = entity.copy(
                category = category,
                nickname = nickname,
                storeUrl = storeUrl,
                lastModified = System.currentTimeMillis(),
                isSynced = false
            )

            clothesDao.updateClothes(updated)
            syncToFirebase(updated)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ### 옷 삭제 ###
    suspend fun deleteClothes(cid: String): Result<Unit> {
        return try {
            val clothes = clothesDao.getClothesById(cid)
                ?: return Result.failure(Exception("Failed to find the clothes."))

            clothesDao.deleteClothesById(cid)

            firebaseClothesRef.child(clothes.ownerUid).child(cid).removeValue().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Firebase 동기화
    private suspend fun syncToFirebase(entity: ClothesEntity) {
        try {
            val ref = firebaseClothesRef
                .child(entity.ownerUid)
                .child(entity.cid)

            val data = mapOf(
                "cid" to entity.cid,
                "category" to entity.category,
                "nickname" to entity.nickname,
                "storeUrl" to entity.storeUrl,
                "createdAt" to entity.createdAt,
                "lastModified" to entity.lastModified
                // imagePath는 동기화하지 않음
            )

            ref.setValue(data).await()
            clothesDao.markAsSynced(entity.cid)
        } catch (e: Exception) {
            // 네트워크 실패 시 isSynced는 false 그대로 (다음에 syncUnsyncedData로 처리)
        }
    }

    // 동기화 되지 않은 데이터 동기화
    suspend fun syncUnsyncedData() {
        val uid = getCurrentUid() ?: return
        val unsynced = clothesDao.getUnsyncedClothes(uid)
        unsynced.forEach { syncToFirebase(it) }
    }

    fun startRealtimeSync(uid: String) {
        val ref = firebaseClothesRef.child(uid)
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // imagePath는 로컬 전용이라 Firebase에서 받아올 필요 없음
                val cid = snapshot.child("cid").getValue(String::class.java) ?: return
                val category = snapshot.child("category").getValue(String::class.java) ?: ""
                val nickname = snapshot.child("nickname").getValue(String::class.java) ?: ""
                val storeUrl = snapshot.child("storeUrl").getValue(String::class.java)
                val createdAt = snapshot.child("createdAt").getValue(Long::class.java) ?: 0L
                val lastModified = snapshot.child("lastModified").getValue(Long::class.java) ?: 0L

                // imagePath는 기존 로컬 값을 유지해야 하므로,
                // 이미 로컬에 존재하면 imagePath를 그대로 유지
                CoroutineScope(Dispatchers.IO).launch {
                    val local = clothesDao.getClothesById(cid)
                    val imagePath = local?.imagePath ?: ""

                    val entity = ClothesEntity(
                        cid = cid,
                        ownerUid = uid,
                        imagePath = imagePath,
                        category = category,
                        nickname = nickname,
                        storeUrl = storeUrl,
                        createdAt = createdAt,
                        isSynced = true,
                        lastModified = lastModified
                    )
                    clothesDao.insertClothes(entity)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                onChildAdded(snapshot, previousChildName)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val cid = snapshot.child("cid").getValue(String::class.java) ?: return
                CoroutineScope(Dispatchers.IO).launch {
                    clothesDao.deleteClothesById(cid)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}