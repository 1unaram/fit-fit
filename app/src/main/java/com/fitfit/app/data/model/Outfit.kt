package com.fitfit.app.data.model

import com.fitfit.app.data.local.entity.OutfitEntity

data class Outfit(
    val oid: String = "",
    val name: String = "",
    val clothesIds: List<String> = emptyList(),  // 옷 ID 리스트
    val createdAt: Long = 0
) {
    constructor() : this("", "", emptyList(), 0)

    companion object {
        fun fromEntity(
            entity: OutfitEntity,
            clothesIds: List<String>
        ): Outfit {
            return Outfit(
                oid = entity.oid,
                name = entity.name,
                clothesIds = clothesIds,
                createdAt = entity.createdAt
            )
        }
    }
}