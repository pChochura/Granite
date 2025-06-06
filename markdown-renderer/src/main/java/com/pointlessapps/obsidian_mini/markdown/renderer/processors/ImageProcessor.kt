package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import androidx.compose.ui.util.fastFirstOrNull
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import com.pointlessapps.obsidian_mini.markdown.renderer.models.toNodeStyles
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal class ImageProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    override fun processMarkers(node: ASTNode): List<NodeMarker> {
        val linkNode = node.children.fastFirstOrNull { it.type == MarkdownElementTypes.INLINE_LINK }

        if (linkNode == null) {
            return emptyList()
        }

        val linkTextMarker = linkNode.children.fastFirstOrNull {
            it.type == MarkdownElementTypes.LINK_TEXT
        }
        val linkDestinationMarker = linkNode.children.fastFirstOrNull {
            it.type == MarkdownElementTypes.LINK_DESTINATION
        }

        if (linkTextMarker == null || linkDestinationMarker == null) {
            return emptyList()
        }

        val openingMarkers = listOfNotNull(
            linkTextMarker.children.fastFirstOrNull { it.type == MarkdownTokenTypes.LBRACKET },
            node.children.fastFirstOrNull { it.type == MarkdownTokenTypes.EXCLAMATION_MARK },
        )
        val closingMarker = linkTextMarker.children.fastFirstOrNull {
            it.type == MarkdownTokenTypes.RBRACKET
        }

        if (openingMarkers.isEmpty() || closingMarker == null) {
            return emptyList()
        }

        return listOf(
            NodeMarker(
                startOffset = openingMarkers.minOf { it.startOffset },
                endOffset = openingMarkers.maxOf { it.endOffset },
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
        val linkNode = node.children.fastFirstOrNull { it.type == MarkdownElementTypes.INLINE_LINK }

        if (linkNode == null) {
            return emptyList()
        }

        val linkTextMarker = linkNode.children.fastFirstOrNull {
            it.type == MarkdownElementTypes.LINK_TEXT
        }
        val linkDestinationMarker = linkNode.children.fastFirstOrNull {
            it.type == MarkdownElementTypes.LINK_DESTINATION
        }

        if (linkTextMarker == null || linkDestinationMarker == null) {
            return emptyList()
        }

        val openingMarkers = listOfNotNull(
            linkTextMarker.children.fastFirstOrNull { it.type == MarkdownTokenTypes.LBRACKET },
            node.children.fastFirstOrNull { it.type == MarkdownTokenTypes.EXCLAMATION_MARK },
        )
        val closingMarker = linkTextMarker.children.fastFirstOrNull {
            it.type == MarkdownTokenTypes.RBRACKET
        }

        if (openingMarkers.isEmpty() || closingMarker == null) {
            return emptyList()
        }

        return styleProvider.styleNodeElement(NodeType.Label, node.type).toNodeStyles(
            startOffset = linkTextMarker.startOffset,
            endOffset = linkTextMarker.endOffset,
        ) + styleProvider.styleNodeElement(NodeType.Decoration, node.type).toNodeStyles(
            startOffset = openingMarkers.minOf { it.startOffset },
            endOffset = openingMarkers.maxOf { it.endOffset },
        ) + styleProvider.styleNodeElement(NodeType.Decoration, node.type).toNodeStyles(
            startOffset = closingMarker.startOffset,
            endOffset = closingMarker.endOffset,
        ) + styleProvider.styleNodeElement(NodeType.Content, node.type).toNodeStyles(
            startOffset = closingMarker.endOffset,
            endOffset = node.endOffset,
        )
    }

    override fun shouldProcessChild(type: IElementType) = false
}
