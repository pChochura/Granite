package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.models.toNodeStyles
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import kotlin.math.max
import kotlin.math.min

internal class HeaderProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    override fun processMarkers(node: ASTNode, textContent: String): List<NodeMarker> {
        val openingMarker = node.children.find { it.type == MarkdownTokenTypes.ATX_HEADER }

        if (openingMarker == null) {
            throw IllegalStateException("HeaderProcessor encountered unbalanced amount of markers.")
        }

        val closingMarker = node.children.findLast { it.type == MarkdownTokenTypes.ATX_HEADER }
            ?.takeIf { it != openingMarker }

        return buildList {
            add(
                NodeMarker(
                    element = MarkdownTokenTypes.ATX_HEADER,
                    startOffset = openingMarker.startOffset,
                    endOffset = min(openingMarker.endOffset + 1, node.endOffset),
                ),
            )

            if (closingMarker != null) {
                add(
                    NodeMarker(
                        element = MarkdownTokenTypes.ATX_HEADER,
                        startOffset = max(node.startOffset, closingMarker.startOffset - 1),
                        endOffset = closingMarker.endOffset,
                    ),
                )
            }
        }
    }

    override fun processStyles(node: ASTNode, textContent: String): List<NodeStyle> {
        val openingMarker = node.children.find { it.type == MarkdownTokenTypes.ATX_HEADER }

        if (openingMarker == null) {
            throw IllegalStateException("HeaderProcessor encountered unbalanced amount of markers.")
        }

        val closingMarker = node.children.findLast { it.type == MarkdownTokenTypes.ATX_HEADER }
            ?.takeIf { it != openingMarker }

        return styleProvider.styleNodeElement(NodeElement.PARAGRAPH, node.type).toNodeStyles(
            startOffset = node.startOffset,
            // Add an additional offset to make the paragraph render smoother
            endOffset = node.endOffset + 1,
        ) + styleProvider.styleNodeElement(NodeElement.CONTENT, node.type).toNodeStyles(
            startOffset = min(openingMarker.endOffset + 1, node.endOffset),
            endOffset = node.endOffset,
        ) + styleProvider.styleNodeElement(NodeElement.DECORATION, node.type).toNodeStyles(
            startOffset = openingMarker.startOffset,
            endOffset = min(openingMarker.endOffset + 1, node.endOffset),
        ) + if (closingMarker != null) {
            styleProvider.styleNodeElement(NodeElement.DECORATION, node.type).toNodeStyles(
                startOffset = max(node.startOffset, closingMarker.startOffset - 1),
                endOffset = closingMarker.endOffset,
            )
        } else emptyList()
    }

    override fun shouldProcessChildren() = true
}
