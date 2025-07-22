package com.pointlessapps.granite.home.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Stable
@Immutable
@Parcelize
@Serializable
internal data class Item(
    val id: Int,
    val parentId: Int?,
    val name: String,
    val updatedAt: Long,
    val createdAt: Long,
    val content: String?,
    val indent: Int,
    val deleted: Boolean,
    val tags: List<Tag>,
) : Parcelable {

    @IgnoredOnParcel
    val isFolder: Boolean = content == null
}
