package com.pointlessapps.granite.domain.note.mapper

import com.pointlessapps.granite.domain.note.model.Note
import com.pointlessapps.granite.datasource.note.model.Note as RemoteNote

internal fun RemoteNote.fromRemote() = Note(
    id = id,
    folderId = folderId,
    name = name,
    updatedAt = updatedAt,
    createdAt = createdAt,
    content = content,
)
