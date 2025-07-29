package com.pointlessapps.granite.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
internal sealed class Property(
    open val id: Int,
    @DrawableRes open val icon: Int,
    @StringRes open val name: Int,
) : Parcelable {
    companion object {
        const val CREATED_AT_ID = -1
        const val UPDATED_AT_ID = -2
        const val TAGS_ID = -3
    }
}

@Stable
@Immutable
@Parcelize
internal class DateProperty(
    override val id: Int,
    @DrawableRes override val icon: Int,
    @StringRes override val name: Int,
    val date: Long,
) : Property(id, icon, name)

@Stable
@Immutable
@Parcelize
internal class ListProperty(
    override val id: Int,
    @DrawableRes override val icon: Int,
    @StringRes override val name: Int,
    val items: List<Item>,
) : Property(id, icon, name) {

    @Stable
    @Immutable
    @Parcelize
    @Serializable
    internal data class Item(
        val id: Int,
        val name: String,
        val color: Int,
    ) : Parcelable
}
