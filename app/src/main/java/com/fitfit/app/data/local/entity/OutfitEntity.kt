package com.fitfit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.fitfit.app.data.local.converters.ListConverter

@Entity(tableName = "outfits")
@TypeConverters(ListConverter::class)
data class OutfitEntity(
    @PrimaryKey
    val oid: String,

    val name: String,

    val ownerUid: String,

    val clothesIds: List<String>,

    val createdAt: Long = System.currentTimeMillis(),

    var isSynced: Boolean = false,

    var lastModified: Long = System.currentTimeMillis()
)
