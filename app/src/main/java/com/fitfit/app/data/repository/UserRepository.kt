package com.fitfit.app.data.repository

import com.fitfit.app.data.local.dao.UserDao
import com.fitfit.app.data.local.entity.UserEntity
import com.fitfit.app.data.model.User
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class UserRepository(private val userDao: UserDao) {
    private val firebaseDB = FirebaseDatabase.getInstance().reference.child("users")

    // Room에서 모든 사용자 가져오기
    fun getAllUsers(): Flow<List<UserEntity>> {
        return userDao.getAllUsers()
    }

    // Room에 사용자 추가 & Firebase 동기화
    suspend fun addUser(user: UserEntity) {
        val userId = userDao.addUser(user)
        syncToFirebase(userId)
    }

    // Firebase로 동기화
    private suspend fun syncToFirebase(userId: Long) {
        try {
            val userEntity = userDao.getUserById(userId) ?: return
            val firebaseUser = User.fromEntity(userEntity, userId.toString())

            firebaseDB.child(userId.toString()).setValue(firebaseUser).await()
            userDao.markAsSynced(userId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}