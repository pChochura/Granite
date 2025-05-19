package com.pointlessapps.obsidian_mini

import androidx.compose.ui.text.AnnotatedString

internal data class NodeStyle(
    val annotation: AnnotatedString.Annotation,
    val startOffset: Int,
    val endOffset: Int,
)

internal fun List<AnnotatedString.Annotation>.toNodeStyles(
    startOffset: Int,
    endOffset: Int,
): List<NodeStyle> = map {
    NodeStyle(
        annotation = it,
        startOffset = startOffset,
        endOffset = endOffset
    )
}
