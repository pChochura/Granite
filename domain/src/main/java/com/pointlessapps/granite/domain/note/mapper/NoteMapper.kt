package com.pointlessapps.granite.domain.note.mapper

import com.pointlessapps.granite.domain.note.model.Note
import com.pointlessapps.granite.domain.utils.formatDateAsTimestamp
import com.pointlessapps.granite.domain.utils.formatTimestampAsDate
import com.pointlessapps.granite.local.datasource.note.entity.NoteEntity as LocalNote
import com.pointlessapps.granite.supabase.datasource.note.model.Note as RemoteNote

internal fun RemoteNote.fromRemote() = Note(
    id = id,
    parentId = parentId,
    name = name,
    updatedAt = updatedAt.formatDateAsTimestamp(),
    createdAt = createdAt.formatDateAsTimestamp(),
    content = content,
)

internal fun LocalNote.fromLocal() = Note(
    id = id,
    parentId = parentId,
    name = name,
    updatedAt = updatedAt.formatDateAsTimestamp(),
    createdAt = createdAt.formatDateAsTimestamp(),
    content = content,
)

internal fun Note.toLocal() = LocalNote(
    id = id,
    parentId = parentId,
    name = name,
    updatedAt = updatedAt.formatTimestampAsDate(),
    createdAt = createdAt.formatTimestampAsDate(),
    content = content,
)
