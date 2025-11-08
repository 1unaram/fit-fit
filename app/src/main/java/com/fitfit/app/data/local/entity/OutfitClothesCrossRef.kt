package com.fitfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "outfit_clothes_cross_ref",
    primaryKeys = ["oid", "cid"]
)
data class OutfitClothesCrossRef(
    @ColumnInfo(name = "oid")
    val oid: String,

    @ColumnInfo(name = "cid")
    val cid: String
)