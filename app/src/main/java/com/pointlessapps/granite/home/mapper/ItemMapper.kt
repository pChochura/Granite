package com.pointlessapps.granite.home.mapper

import com.pointlessapps.granite.home.model.ItemWithParents
import com.pointlessapps.granite.model.Item

internal fun Item.toItemWithParents(parents: List<Item>) = ItemWithParents(
    id = id,
    name = name,
    parentsNames = parents.joinToString("") { it.name + "/" },
)
