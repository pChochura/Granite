package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import androidx.compose.ui.util.fastFlatMap
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.models.toNodeStyles
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal class BlockQuoteProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    // TODO support this wierd case of:
    // > paragraph
    // >
    // And nested block quotes
    override fun processMarkers(node: ASTNode): List<NodeMarker> {
        val openingMarker = node.children.find { it.type == MarkdownTokenTypes.BLOCK_QUOTE }

        if (openingMarker == null) {
            throw IllegalStateException("BlockQuoteProcessor encountered unbalanced amount of markers.")
        }

        val paragraphMarker = node.children.find { it.type == MarkdownElementTypes.PARAGRAPH }

        if (paragraphMarker == null) {
            return listOf(
                NodeMarker(
                    element = MarkdownTokenTypes.GT,
                    startOffset = openingMarker.startOffset,
                    endOffset = openingMarker.endOffset,
                ),
            )
        }

        val decorationMarkers = paragraphMarker.children.filter {
            it.type in listOf(MarkdownTokenTypes.BLOCK_QUOTE, MarkdownTokenTypes.WHITE_SPACE)
        } + openingMarker

        return decorationMarkers.map {
            NodeMarker(
                element = MarkdownTokenTypes.GT,
                startOffset = it.startOffset,
                endOffset = it.endOffset,
            )
        }
    }

    override fun processStyles(node: ASTNode): List<NodeStyle> {
        val openingMarker = node.children.find { it.type == MarkdownTokenTypes.BLOCK_QUOTE }

        if (openingMarker == null) {
            throw IllegalStateException("BlockQuoteProcessor encountered unbalanced amount of markers.")
        }

        val paragraphMarker = node.children.find { it.type == MarkdownElementTypes.PARAGRAPH }

        if (paragraphMarker == null) {
            return styleProvider.styleNodeElement(NodeElement.DECORATION, node.type).toNodeStyles(
                startOffset = openingMarker.startOffset,
                endOffset = openingMarker.endOffset,
            )
        }

        val contentMarkers = paragraphMarker.children.filter { it.type == MarkdownTokenTypes.TEXT }
        val decorationMarkers = paragraphMarker.children.filter {
            it.type in listOf(MarkdownTokenTypes.BLOCK_QUOTE, MarkdownTokenTypes.WHITE_SPACE)
        } + openingMarker

        return decorationMarkers.fastFlatMap {
            styleProvider.styleNodeElement(NodeElement.DECORATION, node.type).toNodeStyles(
                startOffset = it.startOffset,
                endOffset = it.endOffset,
            )
        } + styleProvider.styleNodeElement(NodeElement.CONTENT, node.type).toNodeStyles(
            startOffset = contentMarkers.minOf { it.startOffset },
            endOffset = contentMarkers.maxOf { it.endOffset },
        )
    }

    override fun shouldProcessChildren() = true
}
