package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianTokenTypes
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.models.toNodeStyles
import org.intellij.markdown.ast.ASTNode

internal class CommentBlockProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    // Always show markers
    override fun processMarkers(node: ASTNode) = emptyList<NodeMarker>()

    override fun processStyles(node: ASTNode): List<NodeStyle> {
        val openingMarkers = node.children.takeWhile { it.type == ObsidianTokenTypes.PERCENT }
        val closingMarkers = node.children.takeLastWhile { it.type == ObsidianTokenTypes.PERCENT }

        if (openingMarkers.isEmpty() || closingMarkers.isEmpty()) {
            return emptyList()
        }

        return styleProvider.styleNodeElement(NodeType.DECORATION, node.type).toNodeStyles(
            startOffset = openingMarkers.minOf { it.startOffset },
            endOffset = openingMarkers.maxOf { it.endOffset },
        ) + styleProvider.styleNodeElement(NodeType.DECORATION, node.type).toNodeStyles(
            startOffset = closingMarkers.minOf { it.startOffset },
            endOffset = closingMarkers.maxOf { it.endOffset },
        ) + styleProvider.styleNodeElement(NodeType.CONTENT, node.type).toNodeStyles(
            startOffset = openingMarkers.maxOf { it.endOffset },
            endOffset = closingMarkers.minOf { it.startOffset },
        )
    }

    override fun shouldProcessChildren() = true
}
