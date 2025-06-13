package com.pointlessapps.granite.local.datasource.note.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo("parent_id")
    val parentId: Int?,
    val name: String,
    @ColumnInfo("updated_at", defaultValue = "CURRENT_TIMESTAMP")
    val updatedAt: String,
    @ColumnInfo("created_at", defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: String,
    val content: String?,
    @ColumnInfo("is_deleted", defaultValue = "0")
    val deleted: Boolean,
)
