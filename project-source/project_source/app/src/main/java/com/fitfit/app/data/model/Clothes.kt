package com.fitfit.app.data.model

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
}