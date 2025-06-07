package com.pointlessapps.obsidian_mini.markdown.renderer.utils

import androidx.compose.ui.text.AnnotatedString
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeProcessorResult
import dev.snipme.highlights.internal.isNewLine
import org.intellij.markdown.ast.ASTNode
import java.util.Locale

internal fun NodeProcessor.processNode(
    node: ASTNode,
    hideMarkers: Boolean,
    textContent: String,
) = NodeProcessorResult(
    styles = processStyles(node, textContent),
    markers = if (hideMarkers) processMarkers(node, textContent) else emptyList(),
)

internal fun AnnotatedString.Annotation.withRange(start: Int, end: Int, tag: String? = null) =
    AnnotatedString.Range(this, start, end, tag.orEmpty())

internal fun getIndentRegions(indents: List<Int>): List<Pair<IntRange, Int>> {
    val regions = mutableListOf<Pair<IntRange, Int>>()
    val stack = mutableListOf<Int>()

    for (i in indents.indices) {
        val currentIndent = indents[i]
        // Close regions if current indent is less than stack top
        while (stack.isNotEmpty() && currentIndent < indents[stack.last()]) {
            val start = stack.removeAt(stack.size - 1)
            val indentLevel = indents[start]
            regions.add((start..i - 1) to indentLevel)
        }
        // Start a new region if indentation increases
        if (stack.isEmpty() || currentIndent > indents[stack.last()]) {
            stack.add(i)
        }
    }

    // Close any remaining regions
    while (stack.isNotEmpty()) {
        val start = stack.removeAt(stack.size - 1)
        val indentLevel = indents[start]
        regions.add((start..indents.size - 1) to indentLevel)
    }

    return regions
}

internal fun String.capitalize() = replaceFirstChar {
    if (it.isLowerCase()) {
        it.titlecase(Locale.getDefault())
    } else {
        it.toString()
    }
}

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
