package com.pointlessapps.granite.local.datasource.note.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class NoteWithTagsEntity(
    @Embedded val note: NoteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            NoteTagCrossRef::class,
            parentColumn = "note_id",
            entityColumn = "tag_id",
        ),
    )
    val tags: List<TagEntity>,
)
