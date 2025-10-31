package com.fitfit.app.data.model

import com.fitfit.app.data.local.entity.UserEntity

data class User (
    val id: String = "",
    val username: String = ""
) {
    constructor() : this("", "")

    companion object {
        fun fromEntity(entity: UserEntity, firebaseId: String): User {
            return User (
                id = firebaseId,
                username = entity.username
            )
        }
    }
}