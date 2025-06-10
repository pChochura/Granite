package com.pointlessapps.granite.supabase.datasource.note.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: Int,
    @SerialName("parent_id")
    val parentId: Int?,
    val name: String,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("created_at")
    val createdAt: String,
    val content: String? = null,
)
