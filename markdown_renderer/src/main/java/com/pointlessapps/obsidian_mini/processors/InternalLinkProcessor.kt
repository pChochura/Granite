package com.pointlessapps.obsidian_mini.processors

import com.pointlessapps.obsidian_mini.models.NodeElement
import com.pointlessapps.obsidian_mini.models.NodeMarker
import com.pointlessapps.obsidian_mini.NodeProcessor
import com.pointlessapps.obsidian_mini.models.NodeStyle
import com.pointlessapps.obsidian_mini.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.models.toNodeStyles
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal class InternalLinkProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    override fun processMarkers(node: ASTNode, textContent: String): List<NodeMarker> {
        val openingMarkers = node.children.takeWhile { it.type == MarkdownTokenTypes.LBRACKET }
        val closingMarkers = node.children.takeLastWhile { it.type == MarkdownTokenTypes.RBRACKET }

        if (openingMarkers.isEmpty() || closingMarkers.isEmpty() || openingMarkers.size != closingMarkers.size) {
            throw IllegalStateException("InternalLinkProcessor encountered unbalanced amount of markers.")
        }

        // Flatten multiple subsequent markers into one
        return listOf(
            NodeMarker(
                element = MarkdownTokenTypes.LBRACKET,
                startOffset = openingMarkers.minOf { it.startOffset },
                endOffset = openingMarkers.maxOf { it.endOffset },
            ),
            NodeMarker(
                element = MarkdownTokenTypes.RBRACKET,
                startOffset = closingMarkers.minOf { it.startOffset },
                endOffset = closingMarkers.maxOf { it.endOffset },
            ),
        )
    }

    override fun processStyles(node: ASTNode, textContent: String): List<NodeStyle> {
        val openingMarkers = node.children.takeWhile { it.type == MarkdownTokenTypes.LBRACKET }
        val closingMarkers = node.children.takeLastWhile { it.type == MarkdownTokenTypes.RBRACKET }

        if (openingMarkers.isEmpty() || closingMarkers.isEmpty() || openingMarkers.size != closingMarkers.size) {
            throw IllegalStateException("InternalLinkProcessor encountered unbalanced amount of markers.")
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
        )
    }

    override fun shouldProcessChildren() = true
}
