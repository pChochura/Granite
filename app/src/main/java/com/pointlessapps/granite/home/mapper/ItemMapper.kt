package com.pointlessapps.granite.home.mapper

import com.pointlessapps.granite.domain.model.Note
import com.pointlessapps.granite.home.model.Item
import com.pointlessapps.granite.home.model.ItemWithParents

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

internal fun Item.toItemWithParents(parents: List<Item>) = ItemWithParents(
    id = id,
    name = name,
    parentsNames = parents.joinToString("") { it.name + "/" },
)
