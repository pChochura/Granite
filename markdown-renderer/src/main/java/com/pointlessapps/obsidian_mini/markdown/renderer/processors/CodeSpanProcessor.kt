package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.toNodeStyles
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
