package com.pointlessapps.granite.editor.ui.components

import androidx.annotation.DrawableRes
import com.pointlessapps.granite.markdown.renderer.assist.Style

internal sealed class EditorToolbarItem(
    @DrawableRes val iconRes: Int,
    val tooltip: String,
    val lastActiveStyle: Style?,
    val tag: String,
) {
    class Extended(@DrawableRes iconRes: Int, name: String, lastActiveStyle: Style?, tag: String) :
        EditorToolbarItem(iconRes, name, lastActiveStyle, tag)

    class Simple(@DrawableRes iconRes: Int, tooltip: String, lastActiveStyle: Style?, tag: String) :
        EditorToolbarItem(iconRes, tooltip, lastActiveStyle, tag)

    object Separator : EditorToolbarItem(0, "", null, "")
}
