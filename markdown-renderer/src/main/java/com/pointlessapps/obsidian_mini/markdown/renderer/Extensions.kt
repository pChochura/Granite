package com.pointlessapps.obsidian_mini.markdown.renderer

import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeProcessorResult
import dev.snipme.highlights.internal.isNewLine
import org.intellij.markdown.ast.ASTNode

internal fun NodeProcessor.processNode(
    node: ASTNode,
    hideMarkers: Boolean,
    textContent: String,
) = NodeProcessorResult(
    styles = processStyles(node, textContent),
    markers = if (hideMarkers) processMarkers(node, textContent) else emptyList(),
)

internal fun Int.atLineStart(text: String): Int {
    var i = this
    while (i > 0 && !text[i - 1].isNewLine()) {
        i--
    }

    return i
}

internal fun Int.atLineEnd(text: String): Int {
    var i = this

    // Early return if we are already at the end of the line
    if (i < text.length && text[i].isNewLine()) return this
    while (i < text.length && !text[i].isNewLine()) {
        i++
    }

    return i
}
