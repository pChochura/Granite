package com.pointlessapps.obsidian_mini.processors

import com.pointlessapps.obsidian_mini.NodeElement
import com.pointlessapps.obsidian_mini.NodeMarker
import com.pointlessapps.obsidian_mini.NodeProcessor
import com.pointlessapps.obsidian_mini.NodeStyle
import com.pointlessapps.obsidian_mini.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.toNodeStyles
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal class ItalicProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    override fun processMarkers(node: ASTNode, textContent: String): List<NodeMarker> {
        val openingMarker = node.children.find { it.type == MarkdownTokenTypes.EMPH }
        val closingMarker = node.children.findLast { it.type == MarkdownTokenTypes.EMPH }

        if (openingMarker == null || closingMarker == null || openingMarker == closingMarker) {
            throw IllegalStateException("ItalicProcessor encountered unbalanced amount of markers.")
        }

        return listOf(
            NodeMarker(
                element = MarkdownTokenTypes.EMPH,
                startOffset = openingMarker.startOffset,
                endOffset = openingMarker.endOffset,
            ),
            NodeMarker(
                element = MarkdownTokenTypes.EMPH,
                startOffset = closingMarker.startOffset,
                endOffset = closingMarker.endOffset,
            ),
        )
    }

    override fun processStyles(node: ASTNode, textContent: String): List<NodeStyle> {
        val openingMarkers = node.children.takeWhile { it.type == MarkdownTokenTypes.EMPH }
        val closingMarkers = node.children.takeLastWhile { it.type == MarkdownTokenTypes.EMPH }

        if (openingMarkers.isEmpty() || closingMarkers.isEmpty() || openingMarkers.size != closingMarkers.size) {
            throw IllegalStateException("ItalicProcessor encountered unbalanced amount of markers.")
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
