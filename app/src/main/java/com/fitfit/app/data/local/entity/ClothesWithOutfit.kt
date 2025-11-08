package com.fitfit.app.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ClothesWithOutfit(
    @Embedded
    val clothes: ClothesEntity,

    @Relation(
        parentColumn = "cid",
        entityColumn = "oid",
        associateBy = Junction(OutfitClothesCrossRef::class)
    )
    val outfits: List<OutfitEntity>
)