package com.fitfit.app.data.model

import com.fitfit.app.data.local.entity.OutfitEntity

data class Outfit(
    val oid: String = "",
    val name: String = "",
    val clothesIds: List<String> = emptyList(),  // 옷 ID 리스트
    val createdAt: Long = 0,
    val lastModified: Long = 0
) {
    constructor() : this("", "", emptyList(), 0, 0)

    companion object {
        fun fromEntity(entity: OutfitEntity): Outfit {
            return Outfit(
                oid = entity.oid,
                name = entity.name,
                clothesIds = entity.clothesIds,
                createdAt = entity.createdAt,
                lastModified = entity.lastModified
            )
        }
    }
}