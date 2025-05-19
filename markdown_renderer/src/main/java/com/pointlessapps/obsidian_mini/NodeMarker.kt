package com.pointlessapps.obsidian_mini

import org.intellij.markdown.IElementType

internal data class NodeMarker(
    val element: IElementType,
    val startOffset: Int,
    val endOffset: Int,
) {
    val range: IntRange
        get() = IntRange(startOffset, endOffset)
}
