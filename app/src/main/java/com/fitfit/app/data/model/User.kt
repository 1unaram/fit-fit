package com.fitfit.app.data.model

import com.fitfit.app.data.local.entity.UserEntity

data class User (
    val uid: String = "",
    val username: String = "",
    val password: String = "",
    val createdAt: Long = 0,
    val lastModified: Long = 0
) {
    constructor() : this("", "", "", 0, 0)

    companion object {
        fun fromEntity(entity: UserEntity): User {
            return User (
                uid = entity.uid,
                username = entity.username,
                password = entity.password,
                createdAt = entity.createdAt,
                lastModified = entity.lastModified
            )
        }
    }
}