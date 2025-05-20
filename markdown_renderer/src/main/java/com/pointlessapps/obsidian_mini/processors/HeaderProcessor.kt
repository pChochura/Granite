package com.pointlessapps.obsidian_mini.processors

import com.pointlessapps.obsidian_mini.NodeElement
import com.pointlessapps.obsidian_mini.NodeMarker
import com.pointlessapps.obsidian_mini.NodeProcessor
import com.pointlessapps.obsidian_mini.NodeStyle
import com.pointlessapps.obsidian_mini.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.toNodeStyles
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal class HeaderProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    override fun processMarkers(node: ASTNode, textContent: String): List<NodeMarker> {
        val openingMarker = node.children.find { it.type == MarkdownTokenTypes.ATX_HEADER }

        if (openingMarker == null) {
            throw IllegalStateException("HeaderProcessor encountered unbalanced amount of markers.")
        }

        return listOf(
            NodeMarker(
                element = MarkdownTokenTypes.ATX_HEADER,
                startOffset = openingMarker.startOffset,
                endOffset = openingMarker.endOffset + 1, // Take the following white space into account
            ),
        )
    }

    override fun processStyles(node: ASTNode, textContent: String): List<NodeStyle> {
        val openingMarker = node.children.find { it.type == MarkdownTokenTypes.ATX_HEADER }

        if (openingMarker == null) {
            throw IllegalStateException("HeaderProcessor encountered unbalanced amount of markers.")
        }

        return styleProvider.styleNodeElement(NodeElement.PARAGRAPH, node.type).toNodeStyles(
            startOffset = node.startOffset,
            endOffset = node.endOffset,
        ) + styleProvider.styleNodeElement(NodeElement.CONTENT, node.type).toNodeStyles(
            startOffset = openingMarker.endOffset + 1,
            endOffset = node.endOffset,
        ) + styleProvider.styleNodeElement(NodeElement.DECORATION, node.type).toNodeStyles(
            startOffset = openingMarker.startOffset,
            endOffset = openingMarker.endOffset + 1,
        )
    }

    override fun shouldProcessChildren() = true
}
