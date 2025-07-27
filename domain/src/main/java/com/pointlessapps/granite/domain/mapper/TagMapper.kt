package com.pointlessapps.granite.domain.mapper

import com.pointlessapps.granite.domain.model.Tag
import com.pointlessapps.granite.local.datasource.note.entity.TagEntity as LocalTag

internal fun LocalTag.fromLocal() = Tag(
    id = id,
    name = name,
    color = color,
    isBuiltIn = isBuiltIn,
    isAutomatic = isAutomatic,
)
internal fun Tag.toLocal() = LocalTag(
    id = id,
    name = name,
    color = color,
    isBuiltIn = isBuiltIn,
    isAutomatic = isAutomatic,
)
