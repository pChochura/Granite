package com.pointlessapps.granite.home.utils

import com.pointlessapps.granite.domain.note.model.Note
import com.pointlessapps.granite.home.mapper.toItem
import com.pointlessapps.granite.home.model.Item

internal fun List<Item>.indexToInsert(note: Note, comparator: Comparator<Item>): Int {
    val parentIndex = indexOfFirst { it.id == note.parentId }.takeIf { it >= 0 }
    val indent = (parentIndex?.let(::get)?.indent ?: -1) + 1
    val itemToInsert = note.toItem(indent)
    val isFolder = itemToInsert.isFolder

    var index = (parentIndex ?: -1) + 1
    while (index < size) {
        val currentItem = get(index)
        val currentIndent = currentItem.indent
        if (currentIndent < indent) {
            // Found and item that does not belong to the parent folder
            break
        }

        if (currentIndent == indent && isFolder != currentItem.isFolder) {
            if (isFolder) {
                break
            }

            index++
            continue
        }

        if (
            currentIndent == indent &&
            comparator.compare(itemToInsert, currentItem) < 0
        ) {
            // Found an item that belongs to the parent folder, but is alphabetically before
            break
        }

        index++
    }

    return index
}
