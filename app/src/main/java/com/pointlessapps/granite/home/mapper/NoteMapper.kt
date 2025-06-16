package com.pointlessapps.granite.home.mapper

import com.pointlessapps.granite.domain.note.model.Note
import com.pointlessapps.granite.home.model.Item

internal fun Note.toItem() = Item(
    id = id,
    parentId = parentId,
    name = name,
    updatedAt = updatedAt.toString(),
    createdAt = createdAt.toString(),
    content = content,
    indent = 0,
    deleted = deleted,
)
