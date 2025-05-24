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

internal class InlineLinkProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    override fun processMarkers(node: ASTNode): List<NodeMarker> {
        val linkTextMarker = node.children.fastFirstOrNull {
            it.type == MarkdownElementTypes.LINK_TEXT
        }
        val linkDestinationMarker = node.children.fastFirstOrNull {
            it.type == MarkdownElementTypes.LINK_DESTINATION
        }

        if (linkTextMarker == null || linkDestinationMarker == null) {
            return emptyList()
        }

        val openingMarker = linkTextMarker.children.fastFirstOrNull {
            it.type == MarkdownTokenTypes.LBRACKET
        }
        val closingMarker = linkTextMarker.children.fastFirstOrNull {
            it.type == MarkdownTokenTypes.RBRACKET
        }

        if (openingMarker == null || closingMarker == null) {
            return emptyList()
        }

        return listOf(
            NodeMarker(
                startOffset = openingMarker.startOffset,
                endOffset = openingMarker.endOffset,
            ),
            NodeMarker(
                startOffset = closingMarker.startOffset,
                endOffset = closingMarker.endOffset,
            ),
            NodeMarker(
                startOffset = closingMarker.endOffset,
                endOffset = node.endOffset,
            ),
        )
    }

    override fun processStyles(node: ASTNode): List<NodeStyle> {
        val linkTextMarker = node.children.fastFirstOrNull {
            it.type == MarkdownElementTypes.LINK_TEXT
        }
        val linkDestinationMarker = node.children.fastFirstOrNull {
            it.type == MarkdownElementTypes.LINK_DESTINATION
        }

        if (linkTextMarker == null || linkDestinationMarker == null) {
            return emptyList()
        }

        val openingTextMarker = linkTextMarker.children.fastFirstOrNull {
            it.type == MarkdownTokenTypes.LBRACKET
        }
        val closingTextMarker = linkTextMarker.children.fastFirstOrNull {
            it.type == MarkdownTokenTypes.RBRACKET
        }

        if (openingTextMarker == null || closingTextMarker == null) {
            return emptyList()
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
