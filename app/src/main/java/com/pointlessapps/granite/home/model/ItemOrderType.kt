package com.pointlessapps.granite.home.model

import androidx.annotation.StringRes
import com.pointlessapps.granite.R

internal enum class ItemOrderType(
    @StringRes val label: Int,
    val isAscending: Boolean,
    val comparator: Comparator<Item>,
) {
    NameAscending(
        label = R.string.name_ascending,
        isAscending = true,
        comparator = compareBy { it.name },
    ),
    NameDescending(
        label = R.string.name_descending,
        isAscending = false,
        comparator = compareByDescending { it.name },
    ),

    // TODO fix sorting by date
    CreateDateAscending(
        label = R.string.created_date_ascending,
        isAscending = true,
        comparator = compareBy { it.createdAt },
    ),
    CreateDateDescending(
        R.string.created_date_descending,
        isAscending = false,
        compareByDescending { it.createdAt },
    ),
}
