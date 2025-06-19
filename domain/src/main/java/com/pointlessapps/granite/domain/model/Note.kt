package com.pointlessapps.granite.domain.model

data class Note(
    val id: Int,
    val parentId: Int?,
    val name: String,
    val updatedAt: Long,
    val createdAt: Long,
    val content: String?,
    val deleted: Boolean,
)
