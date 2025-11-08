package com.fitfit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "outfit_clothes_cross_ref",
    primaryKeys = ["oid", "cid"],
    indices = [Index(value = ["cid"])]
)
data class OutfitClothesCrossRef(
    @ColumnInfo(name = "oid")
    val oid: String,

    @ColumnInfo(name = "cid")
    val cid: String
)