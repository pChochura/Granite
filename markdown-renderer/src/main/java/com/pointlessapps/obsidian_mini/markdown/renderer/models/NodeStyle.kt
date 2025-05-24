package com.pointlessapps.obsidian_mini.markdown.renderer.models

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.util.fastMap

internal data class NodeStyle(
    val annotation: AnnotatedString.Annotation,
    val startOffset: Int,
    val endOffset: Int,
)

internal fun List<AnnotatedString.Annotation>.toNodeStyles(
    startOffset: Int,
    endOffset: Int,
): List<NodeStyle> = fastMap {
    NodeStyle(
        annotation = it,
        startOffset = startOffset,
        endOffset = endOffset,
    )
}
