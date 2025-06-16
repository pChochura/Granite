package com.pointlessapps.granite.local.datasource.note.entity

import androidx.room.ColumnInfo

internal data class NoteEntityPartial(
    @ColumnInfo("parent_id")
    val parentId: Int?,
    val name: String,
    @ColumnInfo("updated_at")
    val updatedAt: String,
    @ColumnInfo("created_at")
    val createdAt: String,
    val content: String?,
)
