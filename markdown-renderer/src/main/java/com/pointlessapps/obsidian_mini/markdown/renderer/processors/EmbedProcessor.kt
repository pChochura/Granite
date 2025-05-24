package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFirstOrNull
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.models.toNodeStyles
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal class EmbedProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    override fun processMarkers(node: ASTNode): List<NodeMarker> {
        val exclamationMark = node.children.fastFirstOrNull {
            it.type == MarkdownTokenTypes.EXCLAMATION_MARK
        }
        val openingMarkers = node.children.fastFilter { it.type == MarkdownTokenTypes.LBRACKET }
        val closingMarkers = node.children.takeLastWhile { it.type == MarkdownTokenTypes.RBRACKET }

        if (
            exclamationMark == null ||
            openingMarkers.isEmpty() || closingMarkers.isEmpty() ||
            openingMarkers.size != closingMarkers.size
        ) {
            throw IllegalStateException("EmbedProcessor encountered unbalanced amount of markers.")
        }

        // Flatten multiple subsequent markers into one
        return listOf(
            NodeMarker(
                startOffset = exclamationMark.startOffset,
                endOffset = exclamationMark.endOffset,
            ),
            NodeMarker(
                startOffset = openingMarkers.minOf { it.startOffset },
                endOffset = openingMarkers.maxOf { it.endOffset },
            ),
            NodeMarker(
                startOffset = closingMarkers.minOf { it.startOffset },
                endOffset = closingMarkers.maxOf { it.endOffset },
            ),
        )
    }

    override fun processStyles(node: ASTNode): List<NodeStyle> {
        val openingMarkers = node.children.takeWhile {
            it.type in listOf(MarkdownTokenTypes.EXCLAMATION_MARK, MarkdownTokenTypes.LBRACKET)
        }
        val closingMarkers = node.children.takeLastWhile { it.type == MarkdownTokenTypes.RBRACKET }

        if (openingMarkers.isEmpty() || closingMarkers.isEmpty()) {
            throw IllegalStateException("InternalLinkProcessor encountered unbalanced amount of markers.")
        }

        val labelMarker = node.children.fastFirstOrNull {
            it.type == MarkdownElementTypes.LINK_LABEL
        }

        return styleProvider.styleNodeElement(NodeElement.CONTENT, node.type).toNodeStyles(
            startOffset = openingMarkers.maxOf { it.endOffset },
            endOffset = closingMarkers.minOf { it.startOffset },
        ) + styleProvider.styleNodeElement(NodeElement.DECORATION, node.type).toNodeStyles(
            startOffset = openingMarkers.minOf { it.startOffset },
            endOffset = openingMarkers.maxOf { it.endOffset },
        ) + styleProvider.styleNodeElement(NodeElement.DECORATION, node.type).toNodeStyles(
            startOffset = closingMarkers.minOf { it.startOffset },
            endOffset = closingMarkers.maxOf { it.endOffset },
        ) + if (labelMarker != null) {
            styleProvider.styleNodeElement(NodeElement.LABEL, node.type).toNodeStyles(
                startOffset = labelMarker.startOffset,
                endOffset = labelMarker.endOffset,
            )
        } else emptyList()
    }

    override fun shouldProcessChildren() = true
}
