package com.pointlessapps.obsidian_mini.processors

import com.pointlessapps.obsidian_mini.models.NodeElement
import com.pointlessapps.obsidian_mini.models.NodeMarker
import com.pointlessapps.obsidian_mini.NodeProcessor
import com.pointlessapps.obsidian_mini.models.NodeStyle
import com.pointlessapps.obsidian_mini.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.models.toNodeStyles
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal class InlineLinkProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    override fun processMarkers(node: ASTNode, textContent: String): List<NodeMarker> {
        val linkTextMarker = node.children.find { it.type == MarkdownElementTypes.LINK_TEXT }
        val linkDestinationMarker =
            node.children.find { it.type == MarkdownElementTypes.LINK_DESTINATION }

        if (linkTextMarker == null || linkDestinationMarker == null) {
            throw IllegalStateException("InlineLinkProcessor encountered malformed element.")
        }

        val openingMarker = linkTextMarker.children.find { it.type == MarkdownTokenTypes.LBRACKET }
        val closingMarker = linkTextMarker.children.find { it.type == MarkdownTokenTypes.RBRACKET }

        if (openingMarker == null || closingMarker == null) {
            throw IllegalStateException("InlineLinkProcessor encountered unbalanced amount of markers.")
        }

        return listOf(
            NodeMarker(
                element = MarkdownTokenTypes.LBRACKET,
                startOffset = openingMarker.startOffset,
                endOffset = openingMarker.endOffset,
            ),
            NodeMarker(
                element = MarkdownTokenTypes.RBRACKET,
                startOffset = closingMarker.startOffset,
                endOffset = closingMarker.endOffset,
            ),
            NodeMarker(
                element = MarkdownElementTypes.LINK_DESTINATION,
                startOffset = closingMarker.endOffset,
                endOffset = node.endOffset,
            ),
        )
    }

    override fun processStyles(node: ASTNode, textContent: String): List<NodeStyle> {
        val linkTextMarker = node.children.find { it.type == MarkdownElementTypes.LINK_TEXT }
        val linkDestinationMarker =
            node.children.find { it.type == MarkdownElementTypes.LINK_DESTINATION }

        if (linkTextMarker == null || linkDestinationMarker == null) {
            throw IllegalStateException("InlineLinkProcessor encountered malformed element.")
        }

        val openingTextMarker =
            linkTextMarker.children.find { it.type == MarkdownTokenTypes.LBRACKET }
        val closingTextMarker =
            linkTextMarker.children.find { it.type == MarkdownTokenTypes.RBRACKET }

        if (openingTextMarker == null || closingTextMarker == null) {
            throw IllegalStateException("InlineLinkProcessor encountered unbalanced amount of markers.")
        }

        return styleProvider.styleNodeElement(NodeElement.LABEL, node.type).toNodeStyles(
            startOffset = linkTextMarker.startOffset,
            endOffset = linkTextMarker.endOffset,
        ) + styleProvider.styleNodeElement(NodeElement.DECORATION, node.type).toNodeStyles(
            startOffset = openingTextMarker.startOffset,
            endOffset = openingTextMarker.endOffset,
        ) + styleProvider.styleNodeElement(NodeElement.DECORATION, node.type).toNodeStyles(
            startOffset = closingTextMarker.startOffset,
            endOffset = closingTextMarker.endOffset,
        ) + styleProvider.styleNodeElement(NodeElement.CONTENT, node.type).toNodeStyles(
            startOffset = closingTextMarker.endOffset,
            endOffset = node.endOffset,
        )
    }

    override fun shouldProcessChildren() = true
}
