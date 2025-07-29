package com.pointlessapps.granite.mapper

import com.pointlessapps.granite.model.Item
import com.pointlessapps.granite.model.Tag
import com.pointlessapps.granite.domain.model.Note as DomainNote
import com.pointlessapps.granite.domain.model.Tag as DomainTag


internal fun DomainNote.toItem() = Item(
    id = id,
    parentId = parentId,
    name = name,
    updatedAt = updatedAt,
    createdAt = createdAt,
    content = content,
    indent = 0,
    deleted = deleted,
    tags = tags.map(DomainTag::toTag),
)

internal fun DomainTag.toTag() = Tag(
    id = id,
    name = name,
    color = color,
    isBuiltIn = isBuiltIn,
)
