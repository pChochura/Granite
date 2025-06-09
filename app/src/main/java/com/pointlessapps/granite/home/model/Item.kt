package com.pointlessapps.granite.home.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

@Stable
@Immutable
internal sealed interface Item : Parcelable {
    val id: Int
    val name: String
    val updatedAt: String
    val createdAt: String

    /**
     * Indicate how nested is this item
     */
    val indent: Int
}

@Stable
@Immutable
@Parcelize
internal data class File(
    override val id: Int,
    override val name: String,
    override val updatedAt: String,
    override val createdAt: String,
    override val indent: Int,
    val content: String,
) : Item

@Stable
@Immutable
@Parcelize
internal data class Folder(
    override val id: Int,
    override val name: String,
    override val updatedAt: String,
    override val createdAt: String,
    override val indent: Int,
    val items: List<Item>,
    val opened: Boolean,
) : Item

