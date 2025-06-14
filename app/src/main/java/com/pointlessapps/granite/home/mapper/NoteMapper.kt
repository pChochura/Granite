package com.pointlessapps.granite.home.mapper

import com.pointlessapps.granite.domain.note.model.Note
import com.pointlessapps.granite.home.model.Item
import com.pointlessapps.granite.home.utils.indexToInsert

internal fun List<Note>.toSortedItems(comparator: Comparator<Note>): List<Item> {
    val sortedList = mutableListOf<Item>()
    val notesByParentId = groupBy { it.parentId }

    fun addChildrenOf(parentId: Int?, indent: Int) {
        val children = notesByParentId[parentId] ?: return

        val (folders, notesInLevel) = children.partition { it.content == null }
        folders.sortedWith(comparator).forEach { folder ->
            sortedList.add(folder.toItem(indent))
            addChildrenOf(folder.id, indent + 1)
        }

        sortedList.addAll(notesInLevel.sortedWith(comparator).map { it.toItem(indent) })
    }

    addChildrenOf(null, 0)

    return sortedList
}

internal fun List<Item>.insertSorted(note: Note, comparator: Comparator<Item>): List<Item> {
    val parentIndex = indexOfFirst { it.id == note.parentId }.takeIf { it >= 0 }
    val indent = (parentIndex?.let(::get)?.indent ?: -1) + 1
    val itemToInsert = note.toItem(indent)

    return toMutableList().also { it.add(indexToInsert(note, comparator), itemToInsert) }
}

internal fun Item.toNote() = Note(
    id = id,
    parentId = parentId,
    name = name,
    updatedAt = updatedAt.toLong(),
    createdAt = createdAt.toLong(),
    content = content,
    deleted = deleted,
)

internal fun Note.toItem(indent: Int) = Item(
    id = id,
    parentId = parentId,
    name = name,
    updatedAt = updatedAt.toString(),
    createdAt = createdAt.toString(),
    content = content,
    indent = indent,
    deleted = deleted,
)
