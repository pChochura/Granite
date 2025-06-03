package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastMap
import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianElementTypes
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.models.toNodeStyles
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal class BlockQuoteProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    override fun processMarkers(node: ASTNode): List<NodeMarker> {
        val openingMarkers = node.children.fastFilter { it.type == MarkdownTokenTypes.BLOCK_QUOTE }

        if (openingMarkers.isEmpty()) {
            return emptyList()
        }

        return openingMarkers.fastMap {
            NodeMarker(
                startOffset = it.startOffset,
                endOffset = it.endOffset,
            )
        }
    }

    override fun processStyles(node: ASTNode): List<NodeStyle> {
        val openingMarkers = node.children.fastFilter { it.type == MarkdownTokenTypes.BLOCK_QUOTE }

        if (openingMarkers.isEmpty()) {
            return emptyList()
        }

        val contentMarkers = node.children.fastFilter {
            it.type in listOf(
                ObsidianElementTypes.BLOCK_QUOTE_CONTENT,
                MarkdownElementTypes.PARAGRAPH,
            )
        }

        return openingMarkers.fastFlatMap {
            styleProvider.styleNodeElement(NodeType.DECORATION, node.type).toNodeStyles(
                startOffset = it.startOffset,
                endOffset = it.endOffset,
            )
        } + contentMarkers.fastFlatMap {
            styleProvider.styleNodeElement(NodeType.CONTENT, node.type).toNodeStyles(
                startOffset = it.startOffset,
                endOffset = it.endOffset,
            )
        } + styleProvider.styleNodeElement(NodeType.PARAGRAPH, node.type).toNodeStyles(
            startOffset = node.startOffset,
            // Add an additional offset to make the paragraph render smoother
            endOffset = node.endOffset + 1,
        ) + styleProvider.styleNodeElement(NodeType.WHOLE_NODE, node.type).toNodeStyles(
            startOffset = node.startOffset,
            endOffset = node.endOffset,
        )
    }

    override fun shouldProcessChildren() = true
}
