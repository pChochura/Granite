package com.pointlessapps.granite.editor.ui.components

import androidx.annotation.DrawableRes

internal sealed class EditorToolbarItem(
    @DrawableRes val iconRes: Int,
    val tooltip: String,
    val active: Boolean,
    val tag: String,
) {
    class Extended(@DrawableRes iconRes: Int, name: String, active: Boolean, tag: String) :
        EditorToolbarItem(iconRes, name, active, tag)

    class Simple(@DrawableRes iconRes: Int, tooltip: String, active: Boolean, tag: String) :
        EditorToolbarItem(iconRes, tooltip, active, tag)

    object Separator : EditorToolbarItem(0, "", false, "")
}
