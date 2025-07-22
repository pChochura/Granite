package com.pointlessapps.granite.local.datasource.note.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "notes_tags_cross_ref",
    primaryKeys = ["note_id", "tag_id"],
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["note_id"],
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tag_id"],
        ),
    ],
)
data class NoteTagCrossRef(
    @ColumnInfo(name = "note_id", index = true)
    val noteId: Long,
    @ColumnInfo(name = "tag_id", index = true)
    val tagId: Long,
)
