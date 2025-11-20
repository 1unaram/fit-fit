package com.fitfit.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fitfit.app.data.local.dao.UserDao
import com.fitfit.app.data.local.entity.UserEntity
import com.fitfit.app.data.local.userPrefsDataStore
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

class UserRepository(
    private val userDao: UserDao,
    private val context: Context
) {
    private val firebaseUsersRef = FirebaseDatabase.getInstance().reference.child("users")
    private val firebaseUsernamesRef = FirebaseDatabase.getInstance().reference.child("usernames")
    private val idGenerator = IdGenerator(context)

    private suspend fun getCurrentUid(): String? {
        return context.userPrefsDataStore.data.map {
            it[stringPreferencesKey("current_uid")]
        }.first()
    }

    // Firebase에서 username 중복 체크
    suspend fun isUsernameTaken(username: String): Boolean {
        return try {
            val snapshot = firebaseUsernamesRef.child(username).get().await()
            snapshot.exists()
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }
    }

    // 회원가입
    suspend fun registerUser(username: String, password: String): Result<String> {
        return try {
            // 1. 중복 체크
            if (isUsernameTaken(username)) {
                return Result.failure(Exception("Already taken username."))
            }

            // 2. uid 생성
            val uid = idGenerator.generateNextUserId()

            // 3. UserEntity 생성
            val user = UserEntity(
                uid = uid,
                username = username,
                password = password,
                isSynced = false
            )

            // 4. Room에 저장
            userDao.insertUser(user)

            // 5. Firebase에 저장 (Transaction)
            val updates = hashMapOf<String, Any>(
                "/users/$uid" to mapOf(
                    "uid" to uid,
                    "username" to username,
                    "password" to password,
                    "createdAt" to user.createdAt,
                    "lastModified" to user.lastModified
                ),
                "/usernames/$username" to uid
            )

            FirebaseDatabase.getInstance().reference.updateChildren(updates).await()

            // 6. 동기화 완료 표시
            userDao.markAsSynced(uid, System.currentTimeMillis())

            Result.success(uid)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // 로그인
    suspend fun loginUser(username: String, password: String): Result<UserEntity> {
        return try {
            // 1. Firebase에서 uid 찾기
            val uidSnapshot = firebaseUsernamesRef.child(username).get().await()

            if (!uidSnapshot.exists()) {
                return Result.failure(Exception("No username found."))
            }

            val uid = uidSnapshot.value as? String
                ?: return Result.failure(Exception("Invalid uid value."))

            // 2. Firebase에서 사용자 정보 가져오기
            val userSnapshot = firebaseUsersRef.child(uid).get().await()

            if (!userSnapshot.exists()) {
                return Result.failure(Exception("No user data found."))
            }

            val firebaseUsername = userSnapshot.child("username").value as? String ?: ""
            val firebasePassword = userSnapshot.child("password").value as? String ?: ""
            val firebaseCreatedAt = userSnapshot.child("createdAt").value as? Long ?: 0L
            val firebaseLastModified = userSnapshot.child("lastModified").value as? Long ?: 0L

            // 3. 비밀번호 확인
            if (firebasePassword != password) {
                return Result.failure(Exception("Incorrect password."))
            }

            val user = UserEntity(
                uid = uid,
                username = firebaseUsername,
                password = firebasePassword,
                createdAt = firebaseCreatedAt,
                lastModified = firebaseLastModified,
                isSynced = true
            )

            // 4. Room에 저장
            userDao.insertUser(user)

            // 5. DataStore에 현재 uid 저장
            context.userPrefsDataStore.edit { prefs ->
                prefs[stringPreferencesKey("current_uid")] = uid
                prefs[booleanPreferencesKey("is_logged_in")] = true
            }

            Result.success(user)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // 로그아웃
    suspend fun logout() {
        val currentUid = getCurrentUid()

        // DataStore 초기화
        context.userPrefsDataStore.edit { it.clear() }

        // 필요시 Room 데이터 삭제
        currentUid?.let {
            // userDao.deleteUserById(it) // 필요하면 활성화
        }
    }

    // 실시간 동기화
    fun startRealtimeSync() {
        firebaseUsersRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                syncUserFromFirebase(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                syncUserFromFirebase(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val uid = snapshot.key ?: return
                CoroutineScope(Dispatchers.IO).launch {
                    userDao.deleteUserById(uid)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun syncUserFromFirebase(snapshot: DataSnapshot) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val uid = snapshot.child("uid").value as? String ?: return@launch
                val username = snapshot.child("username").value as? String ?: ""
                val password = snapshot.child("password").value as? String ?: ""
                val createdAt = snapshot.child("createdAt").value as? Long ?: 0L
                val lastModified = snapshot.child("lastModified").value as? Long ?: 0L

                val user = UserEntity(
                    uid = uid,
                    username = username,
                    password = password,
                    createdAt = createdAt,
                    lastModified = lastModified,
                    isSynced = true
                )

                userDao.insertUser(user)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}