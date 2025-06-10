package com.pointlessapps.granite.datasource.note.model

import com.pointlessapps.granite.datasource.utils.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: Int,
    @SerialName("parent_id")
    val parentId: Int?,
    val name: String,
    @SerialName("updated_at")
    @Serializable(DateSerializer::class)
    val updatedAt: Long,
    @SerialName("created_at")
    @Serializable(DateSerializer::class)
    val createdAt: Long,
    val content: String? = null,
)
