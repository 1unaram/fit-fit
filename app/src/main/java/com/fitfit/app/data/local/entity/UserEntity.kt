package com.fitfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    val id: Long = 0,

    @ColumnInfo(name = "username")
    var username: String,

    @ColumnInfo(name = "is_synced")
    var isSynced: Boolean = false
)

