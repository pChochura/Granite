package com.pointlessapps.granite.home.model

import androidx.annotation.StringRes
import com.pointlessapps.granite.R

internal enum class ItemOrderType(
    @StringRes val label: Int,
    val comparator: Comparator<Item>,
) {
    NameAscending(R.string.name_ascending, compareBy { it.name }),
    NameDescending(R.string.name_descending, compareByDescending { it.name }),

    // TODO fix sorting by date
    CreateDateAscending(R.string.created_date_ascending, compareBy { it.createdAt }),
    CreateDateDescending(R.string.created_date_descending, compareByDescending { it.createdAt }),
    UpdateDateAscending(R.string.updated_date_ascending, compareBy { it.updatedAt }),
    UpdateDateDescending(R.string.updated_date_descending, compareByDescending { it.updatedAt });
}
