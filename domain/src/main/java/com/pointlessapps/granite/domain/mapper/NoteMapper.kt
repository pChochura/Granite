package com.pointlessapps.granite.domain.mapper

import com.pointlessapps.granite.domain.model.Note
import com.pointlessapps.granite.domain.model.Tag
import com.pointlessapps.granite.domain.utils.formatDateAsTimestamp
import com.pointlessapps.granite.domain.utils.formatTimestampAsDate
import com.pointlessapps.granite.local.datasource.note.entity.NoteEntity
import com.pointlessapps.granite.local.datasource.note.entity.TagEntity
import com.pointlessapps.granite.local.datasource.note.entity.NoteWithTagsEntity as LocalNote
import com.pointlessapps.granite.supabase.datasource.note.model.Note as RemoteNote

internal fun RemoteNote.fromRemote() = Note(
    id = id,
    parentId = parentId,
    name = name,
    updatedAt = updatedAt.formatDateAsTimestamp(),
    createdAt = createdAt.formatDateAsTimestamp(),
    content = content,
    deleted = deleted,
    tags = emptyList(),
)

internal fun LocalNote.fromLocal() = Note(
    id = note.id,
    parentId = note.parentId,
    name = note.name,
    updatedAt = note.updatedAt.formatDateAsTimestamp(),
    createdAt = note.createdAt.formatDateAsTimestamp(),
    content = note.content,
    deleted = note.deleted,
    tags = tags.map(TagEntity::fromLocal),
)

internal fun Note.toLocal() = LocalNote(
    note = NoteEntity(
        id = id,
        parentId = parentId,
        name = name,
        updatedAt = updatedAt.formatTimestampAsDate(),
        createdAt = createdAt.formatTimestampAsDate(),
        content = content,
        deleted = deleted,
    ),
    tags = tags.map(Tag::toLocal),
)
