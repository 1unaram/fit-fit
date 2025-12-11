package com.fitfit.app.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class OutfitWithClothes(
    @Embedded
    val outfit: OutfitEntity,

    @Relation(
        parentColumn = "oid",
        entityColumn = "cid",
        associateBy = Junction(OutfitClothesCrossRef::class)
    )
    val clothes: List<ClothesEntity>
)