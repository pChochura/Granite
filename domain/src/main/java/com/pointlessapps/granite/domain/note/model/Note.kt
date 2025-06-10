package com.pointlessapps.granite.domain.note.model

data class Note(
    val id: Int,
    val parentId: Int?,
    val name: String,
    val updatedAt: Long,
    val createdAt: Long,
    val content: String?,
)
