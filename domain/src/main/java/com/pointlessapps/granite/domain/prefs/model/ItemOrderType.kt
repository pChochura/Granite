package com.pointlessapps.granite.domain.prefs.model

enum class ItemOrderType(val index: Int) {
    NameAscending(0), NameDescending(1),
    CreateDateAscending(2), CreateDateDescending(3),
    UpdateDateAscending(4), UpdateDateDescending(5);

    companion object {
        fun fromIndex(index: Int) = entries.firstOrNull { it.index == index } ?: NameAscending
    }
}
