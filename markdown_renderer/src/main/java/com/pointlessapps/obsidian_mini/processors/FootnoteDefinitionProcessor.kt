package com.pointlessapps.obsidian_mini.processors

import androidx.compose.ui.util.fastMapNotNull
import com.pointlessapps.obsidian_mini.NodeElement
import com.pointlessapps.obsidian_mini.NodeMarker
import com.pointlessapps.obsidian_mini.NodeProcessor
import com.pointlessapps.obsidian_mini.NodeStyle
import com.pointlessapps.obsidian_mini.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.flavours.obsidian.ObsidianTokenTypes
import com.pointlessapps.obsidian_mini.toNodeStyles
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal class FootnoteDefinitionProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    private companion object {
        const val NUMBER_OF_MARKERS = 4
    }

    override fun processMarkers(node: ASTNode, textContent: String) = node.children.fastMapNotNull {
        when (it.type) {
            MarkdownTokenTypes.LBRACKET -> NodeMarker(it.type, it.startOffset, it.endOffset)
            ObsidianTokenTypes.CARET -> NodeMarker(it.type, it.startOffset, it.endOffset)
            MarkdownTokenTypes.RBRACKET -> NodeMarker(it.type, it.startOffset, it.endOffset)
            MarkdownTokenTypes.COLON -> NodeMarker(it.type, it.startOffset, it.endOffset)
            else -> null
        }
    }.also {
        if (it.size != NUMBER_OF_MARKERS) throw IllegalStateException(
            "FootnoteDefinitionProcessor encountered incorrect amount of markers." +
                    "Expected: $NUMBER_OF_MARKERS, got: ${it.size}",
        )
    }

    override fun processStyles(node: ASTNode, textContent: String): List<NodeStyle> {
        val openingMarkers = node.children.takeWhile {
            it.type in listOf(MarkdownTokenTypes.LBRACKET, ObsidianTokenTypes.CARET)
        }
        val closingMarkers = node.children.filter {
            it.type in listOf(
                MarkdownTokenTypes.RBRACKET,
                MarkdownTokenTypes.COLON,
            )
        }

        if (openingMarkers.isEmpty() || closingMarkers.isEmpty()) {
            throw IllegalStateException("FootnoteDefinitionProcessor encountered unbalanced amount of markers.")
        }

        return styleProvider.styleNodeElement(NodeElement.LABEL, node.type).toNodeStyles(
            startOffset = openingMarkers.maxOf { it.endOffset },
            endOffset = closingMarkers.minOf { it.startOffset },
        ) + styleProvider.styleNodeElement(NodeElement.DECORATION, node.type).toNodeStyles(
            startOffset = openingMarkers.minOf { it.startOffset },
            endOffset = openingMarkers.maxOf { it.endOffset },
        ) + styleProvider.styleNodeElement(NodeElement.DECORATION, node.type).toNodeStyles(
            startOffset = closingMarkers.minOf { it.startOffset },
            endOffset = closingMarkers.maxOf { it.endOffset } + 1,
        ) + styleProvider.styleNodeElement(NodeElement.CONTENT, node.type).toNodeStyles(
            startOffset = closingMarkers.maxOf { it.endOffset } + 1,
            endOffset = node.endOffset,
        )
    }

    override fun shouldProcessChildren() = true
}
