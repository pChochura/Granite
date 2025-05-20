package com.pointlessapps.obsidian_mini.processors

import com.pointlessapps.obsidian_mini.NodeElement
import com.pointlessapps.obsidian_mini.NodeMarker
import com.pointlessapps.obsidian_mini.NodeProcessor
import com.pointlessapps.obsidian_mini.NodeStyle
import com.pointlessapps.obsidian_mini.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.flavours.obsidian.ObsidianTokenTypes
import com.pointlessapps.obsidian_mini.toNodeStyles
import org.intellij.markdown.ast.ASTNode

internal class HighlightProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    override fun processMarkers(node: ASTNode, textContent: String): List<NodeMarker> {
        val openingMarkers = node.children.takeWhile { it.type == ObsidianTokenTypes.EQ }
        val closingMarkers = node.children.takeLastWhile { it.type == ObsidianTokenTypes.EQ }

        if (openingMarkers.isEmpty() || closingMarkers.isEmpty() || openingMarkers.size != closingMarkers.size) {
            throw IllegalStateException("HighlightProcessor encountered unbalanced amount of markers.")
        }

        // Flatten multiple subsequent markers into one
        return listOf(
            NodeMarker(
                element = ObsidianTokenTypes.EQ,
                startOffset = openingMarkers.minOf { it.startOffset },
                endOffset = openingMarkers.maxOf { it.endOffset },
            ),
            NodeMarker(
                element = ObsidianTokenTypes.EQ,
                startOffset = closingMarkers.minOf { it.startOffset },
                endOffset = closingMarkers.maxOf { it.endOffset },
            ),
        )
    }

    override fun processStyles(node: ASTNode, textContent: String): List<NodeStyle> {
        val openingMarkers = node.children.takeWhile { it.type == ObsidianTokenTypes.EQ }
        val closingMarkers = node.children.takeLastWhile { it.type == ObsidianTokenTypes.EQ }

        if (openingMarkers.isEmpty() || closingMarkers.isEmpty() || openingMarkers.size != closingMarkers.size) {
            throw IllegalStateException("HighlightProcessor encountered unbalanced amount of markers.")
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
