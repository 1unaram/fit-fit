package com.fitfit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothes")
data class ClothesEntity(
    @PrimaryKey
    val cid: String,

    val ownerUid: String,

    val name: String,

    val category: String,

    val createdAt: Long = System.currentTimeMillis(),

    val isSynced: Boolean = false,

    val lastModified: Long = System.currentTimeMillis()
)