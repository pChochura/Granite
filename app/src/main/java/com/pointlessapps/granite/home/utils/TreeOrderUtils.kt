package com.pointlessapps.granite.home.utils

import com.pointlessapps.granite.home.model.Item

internal fun List<Item>.toSortedTree(comparator: Comparator<Item>): List<Item> {
    val sortedList = mutableListOf<Item>()
    val notesIds = map { it.id }.toSet()
    val notesByParentId = groupBy { it.parentId }

    fun addChildrenOf(parentId: Int?, indent: Int) {
        val children = notesByParentId[parentId] ?: return

        val (folders, notesInLevel) = children.partition { it.content == null }
        folders.sortedWith(comparator).forEach { folder ->
            sortedList.add(folder.copy(indent = indent))
            addChildrenOf(folder.id, indent + 1)
        }

        sortedList.addAll(notesInLevel.sortedWith(comparator).map { it.copy(indent = indent) })
    }

    addChildrenOf(null, 0)

    // Add orphan notes and folders
    notesByParentId.keys.filter { it != null && it !in notesIds }.forEach { addChildrenOf(it, 0) }

    return sortedList
}

internal fun List<Item>.childrenOf(folder: Item) = drop(indexOf(folder) + 1)
    .takeWhile { it.indent > folder.indent }
