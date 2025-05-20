package com.pointlessapps.obsidian_mini.processors

import com.pointlessapps.obsidian_mini.NodeElement
import com.pointlessapps.obsidian_mini.NodeMarker
import com.pointlessapps.obsidian_mini.NodeProcessor
import com.pointlessapps.obsidian_mini.NodeStyle
import com.pointlessapps.obsidian_mini.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.toNodeStyles
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal class CodeSpanProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    override fun processMarkers(node: ASTNode, textContent: String): List<NodeMarker> {
        val openingMarker = node.children.find { it.type == MarkdownTokenTypes.BACKTICK }
        val closingMarker = node.children.findLast { it.type == MarkdownTokenTypes.BACKTICK }

        if (openingMarker == null || closingMarker == null || openingMarker == closingMarker) {
            throw IllegalStateException("CodeSpanProcessor encountered unbalanced amount of markers.")
        }

        return listOf(
            NodeMarker(
                element = MarkdownTokenTypes.BACKTICK,
                startOffset = openingMarker.startOffset,
                endOffset = openingMarker.endOffset,
            ),
            NodeMarker(
                element = MarkdownTokenTypes.BACKTICK,
                startOffset = closingMarker.startOffset,
                endOffset = closingMarker.endOffset,
            ),
        )
    }

    override fun processStyles(node: ASTNode, textContent: String): List<NodeStyle> {
        val openingMarker = node.children.find { it.type == MarkdownTokenTypes.BACKTICK }
        val closingMarker = node.children.findLast { it.type == MarkdownTokenTypes.BACKTICK }

        if (openingMarker == null || closingMarker == null || openingMarker == closingMarker) {
            throw IllegalStateException("CodeSpanProcessor encountered unbalanced amount of markers.")
        }

        return styleProvider.styleNodeElement(NodeElement.CONTENT, node.type).toNodeStyles(
            startOffset = openingMarker.endOffset,
            endOffset = closingMarker.startOffset,
        ) + styleProvider.styleNodeElement(NodeElement.DECORATION, node.type).toNodeStyles(
            startOffset = openingMarker.startOffset,
            endOffset = openingMarker.endOffset,
        ) + styleProvider.styleNodeElement(NodeElement.DECORATION, node.type).toNodeStyles(
            startOffset = closingMarker.startOffset,
            endOffset = closingMarker.endOffset,
        )
    }

    override fun shouldProcessChildren() = true
}
