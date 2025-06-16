package com.pointlessapps.granite.local.datasource.note.entity

import androidx.room.ColumnInfo

internal data class NoteEntityParentIdPartial(
    val id: Int,
    @ColumnInfo("parent_id")
    val parentId: Int?,
)
