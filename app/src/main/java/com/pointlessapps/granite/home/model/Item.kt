package com.pointlessapps.granite.home.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Stable
@Immutable
@Parcelize
internal data class Item(
    val id: Int,
    val parentId: Int?,
    val name: String,
    val updatedAt: String,
    val createdAt: String,
    val content: String?,
    val indent: Int,
) : Parcelable {

    @IgnoredOnParcel
    val isFolder: Boolean = content == null
}
