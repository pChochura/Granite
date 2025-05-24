package com.pointlessapps.obsidian_mini.markdown.renderer.processors

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

internal class InternalLinkProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    override fun processMarkers(node: ASTNode): List<NodeMarker> {
        val openingMarkers = node.children.takeWhile { it.type == MarkdownTokenTypes.LBRACKET }
        val closingMarkers = node.children.takeLastWhile { it.type == MarkdownTokenTypes.RBRACKET }

        if (openingMarkers.isEmpty() || closingMarkers.isEmpty() || openingMarkers.size != closingMarkers.size) {
            return emptyList()
        }

        val labelMarker = node.children.fastFirstOrNull {
            it.type == MarkdownElementTypes.LINK_LABEL
        }

        // Flatten multiple subsequent markers into one
        return listOfNotNull(
            NodeMarker(
                startOffset = openingMarkers.minOf { it.startOffset },
                endOffset = openingMarkers.maxOf { it.endOffset },
            ),
            NodeMarker(
                startOffset = closingMarkers.minOf { it.startOffset },
                endOffset = closingMarkers.maxOf { it.endOffset },
            ),
            if (labelMarker != null) {
                NodeMarker(
                    startOffset = openingMarkers.maxOf { it.endOffset },
                    endOffset = labelMarker.startOffset,
                )
            } else null,
        )
    }

    override fun processStyles(node: ASTNode): List<NodeStyle> {
        val openingMarkers = node.children.takeWhile { it.type == MarkdownTokenTypes.LBRACKET }
        val closingMarkers = node.children.takeLastWhile { it.type == MarkdownTokenTypes.RBRACKET }

        if (openingMarkers.isEmpty() || closingMarkers.isEmpty() || openingMarkers.size != closingMarkers.size) {
            return emptyList()
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
