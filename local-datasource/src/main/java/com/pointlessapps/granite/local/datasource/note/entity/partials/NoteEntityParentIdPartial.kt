package com.pointlessapps.granite.local.datasource.note.entity.partials

import androidx.room.ColumnInfo

internal data class NoteEntityParentIdPartial(
    val id: Int,
    @ColumnInfo("parent_id")
    val parentId: Int?,
    @ColumnInfo("updated_at")
    val updatedAt: String,
)
