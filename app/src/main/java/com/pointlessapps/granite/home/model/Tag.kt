package com.pointlessapps.granite.home.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Stable
@Immutable
@Parcelize
@Serializable
data class Tag(
    val id: Int,
    val name: String,
    val color: Int,
    val isBuiltIn: Boolean,
) : Parcelable
