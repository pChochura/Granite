package com.pointlessapps.granite.home.mapper

import com.pointlessapps.granite.domain.note.model.Note
import com.pointlessapps.granite.home.model.Item

internal fun List<Note>.toSortedItems(): List<Item> {
    val sortedList = mutableListOf<Item>()
    val notesByParentId = groupBy { it.parentId }

    fun addChildrenOf(parentId: Int?, indent: Int) {
        val children = notesByParentId[parentId] ?: return

        val (folders, notesInLevel) = children.partition { it.content == null }
        folders.sortedBy { it.name }.forEach { folder ->
            sortedList.add(folder.toItem(indent))
            addChildrenOf(folder.id, indent + 1)
        }

        sortedList.addAll(notesInLevel.sortedBy { it.name }.map { it.toItem(indent) })
    }

    addChildrenOf(null, 0)

    return sortedList
}

internal fun Note.toItem(indent: Int) = Item(
    id = id,
    parentId = parentId,
    name = name,
    updatedAt = updatedAt.toString(),
    createdAt = createdAt.toString(),
    content = content,
    indent = indent,
)
