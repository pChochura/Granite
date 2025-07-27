package com.pointlessapps.granite.local.datasource.note.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val color: Int,
    @ColumnInfo("is_built_in")
    val isBuiltIn: Boolean,
    @ColumnInfo("is_automatic")
    val isAutomatic: Boolean,
)
