package com.pointlessapps.obsidian_mini.markdown.renderer.models

import org.intellij.markdown.IElementType

internal data class NodeMarker(
    val element: IElementType,
    val startOffset: Int,
    val endOffset: Int,
)
