package com.pointlessapps.granite.home.mapper

import com.pointlessapps.granite.domain.note.model.Note
import com.pointlessapps.granite.home.model.Item

internal fun Comparator<Item>.toNoteComparator(): Comparator<Note> = object : Comparator<Note> {
    override fun compare(
        o1: Note?,
        o2: Note?,
    ): Int {
        if (o1 == null && o2 == null) return 0
        if (o1 == null) return -1
        if (o2 == null) return 1

        return this@toNoteComparator.compare(o1.toItem(0), o2.toItem(0))
    }
}
