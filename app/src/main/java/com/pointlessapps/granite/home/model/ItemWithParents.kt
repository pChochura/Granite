package com.pointlessapps.granite.home.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

@Stable
@Immutable
@Parcelize
internal data class ItemWithParents(
    val id: Int?,
    val name: String,
    val parentsNames: String,
) : Parcelable {
    override fun toString() = "${parentsNames}${name}"
}
