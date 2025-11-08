package com.fitfit.app.data.model

import com.fitfit.app.data.local.entity.ClothesEntity

data class Clothes (
    val cid: String = "",
    val name: String = "",
    val category: String = "",
    val createdAt: Long = 0,
    val lastModified: Long = 0
) {
    constructor() : this(
        cid = "",
        name = "",
        category = "",
        createdAt = 0,
        lastModified = 0
    )

    companion object {
        fun fromEntity(entity: ClothesEntity): Clothes {
            return Clothes(
                cid = entity.cid,
                name = entity.name,
                category = entity.category,
                createdAt = entity.createdAt,
                lastModified = entity.lastModified
            )
        }
    }
}