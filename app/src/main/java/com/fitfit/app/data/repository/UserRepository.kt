package com.fitfit.app.data.repository

import android.content.Context
import com.fitfit.app.data.local.dao.UserDao
import com.fitfit.app.data.local.entity.UserEntity
import com.fitfit.app.data.model.User
import com.fitfit.app.data.util.IdGenerator
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val userDao: UserDao,
    private val context: Context
) {
    private val firebaseDB = FirebaseDatabase.getInstance().reference.child("users")
    private val idGenerator = IdGenerator(context)

    // Room에서 모든 사용자 가져오기
    fun getAllUsers(): Flow<List<UserEntity>> {
        return userDao.getAllUsers()
    }

    // Room에 사용자 추가 & Firebase 동기화
    suspend fun insertUser(username: String, password: String) {
        val uid = idGenerator.generateNextUserId()
        val user = UserEntity(uid = uid, username = username, password = password)
        userDao.insertUser(user)
        syncToFirebase(uid)
    }

    // Username 중복 검사
    suspend fun isUsernameTaken(username: String): Boolean {
        return userDao.getUserByUsername(username) != null
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user.copy(isSynced = false))
        syncToFirebase(user.uid)
    }

    // Firebase로 동기화
    private suspend fun syncToFirebase(uid: String) {
        try {
            val userEntity = userDao.getUserById(uid) ?: return
            val firebaseUser = User.fromEntity(userEntity)

            firebaseDB.child(uid).setValue(firebaseUser).await()
            userDao.markAsSynced(uid)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun deleteFromFirebase(uid: String) {
        try {
            firebaseDB.child(uid).removeValue().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 실시간 삭제 동기화 시작
    fun startRealtimeDeleteSync() {
        firebaseDB.addChildEventListener(object : ChildEventListener {
            override fun onChildRemoved(snapshot: DataSnapshot) {
                val uid = snapshot.key ?: return
                // Room DB에서 삭제
                CoroutineScope(Dispatchers.IO).launch {
                    userDao.deleteUserById(uid)
                }
            }
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}