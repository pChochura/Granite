package com.pointlessapps.granite.home.mapper

import com.pointlessapps.granite.home.model.Item
import com.pointlessapps.granite.home.model.ItemWithParents
import com.pointlessapps.granite.home.model.Tag
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

internal fun Item.toItemWithParents(parents: List<Item>) = ItemWithParents(
    id = id,
    name = name,
    parentsNames = parents.joinToString("") { it.name + "/" },
)

internal fun DomainTag.toTag() = Tag(
    id = id,
    name = name,
    color = color,
    isBuiltIn = isBuiltIn,
)
