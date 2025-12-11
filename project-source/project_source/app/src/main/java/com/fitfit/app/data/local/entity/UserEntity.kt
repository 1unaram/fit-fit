package com.fitfit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    var uid: String,

    var username: String,

    var password: String,

    val createdAt: Long = System.currentTimeMillis(),

    var isSynced: Boolean = false,

    var lastModified: Long = System.currentTimeMillis()
)

