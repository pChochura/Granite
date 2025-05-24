package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianTokenTypes
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.models.toNodeStyles
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMTokenTypes

internal open class DelimiterProcessor(
    styleProvider: ProcessorStyleProvider,
    private val delimiter: IElementType,
    private val alwaysShowMarkers: Boolean = false,
) : NodeProcessor(styleProvider) {

    override fun processMarkers(node: ASTNode): List<NodeMarker> {
        if (alwaysShowMarkers) return emptyList()

        val openingMarkers = node.children.takeWhile { it.type == delimiter }
        val closingMarkers = node.children.takeLastWhile { it.type == delimiter }

        if (openingMarkers.isEmpty() || closingMarkers.isEmpty() || openingMarkers.size != closingMarkers.size) {
            return emptyList()
        }

        // Flatten multiple subsequent markers into one
        return listOf(
            NodeMarker(
                startOffset = openingMarkers.minOf { it.startOffset },
                endOffset = openingMarkers.maxOf { it.endOffset },
            ),
            NodeMarker(
                startOffset = closingMarkers.minOf { it.startOffset },
                endOffset = closingMarkers.maxOf { it.endOffset },
            ),
        )
    }

    override fun processStyles(node: ASTNode): List<NodeStyle> {
        val openingMarkers = node.children.takeWhile { it.type == delimiter }
        val closingMarkers = node.children.takeLastWhile { it.type == delimiter }

        if (openingMarkers.isEmpty() || closingMarkers.isEmpty() || openingMarkers.size != closingMarkers.size) {
            return emptyList()
        }

        return styleProvider.styleNodeElement(NodeElement.CONTENT, node.type).toNodeStyles(
            startOffset = openingMarkers.maxOf { it.endOffset },
            endOffset = closingMarkers.minOf { it.startOffset },
        ) + styleProvider.styleNodeElement(NodeElement.DECORATION, node.type).toNodeStyles(
            startOffset = openingMarkers.minOf { it.startOffset },
            endOffset = openingMarkers.maxOf { it.endOffset },
        ) + styleProvider.styleNodeElement(NodeElement.DECORATION, node.type).toNodeStyles(
            startOffset = closingMarkers.minOf { it.startOffset },
            endOffset = closingMarkers.maxOf { it.endOffset },
        )
    }

    override fun shouldProcessChildren() = true
}

internal class BoldProcessor(
    styleProvider: ProcessorStyleProvider,
) : DelimiterProcessor(styleProvider, MarkdownTokenTypes.EMPH)

internal class HighlightProcessor(
    styleProvider: ProcessorStyleProvider,
) : DelimiterProcessor(styleProvider, ObsidianTokenTypes.EQ)

internal class ItalicProcessor(
    styleProvider: ProcessorStyleProvider,
) : DelimiterProcessor(styleProvider, MarkdownTokenTypes.EMPH)

internal class StrikethroughProcessor(
    styleProvider: ProcessorStyleProvider,
) : DelimiterProcessor(styleProvider, GFMTokenTypes.TILDE)

internal class CommentProcessor(
    styleProvider: ProcessorStyleProvider,
) : DelimiterProcessor(styleProvider, ObsidianTokenTypes.PERCENT, alwaysShowMarkers = true)

internal class CodeSpanProcessor(
    styleProvider: ProcessorStyleProvider,
) : DelimiterProcessor(styleProvider, MarkdownTokenTypes.BACKTICK)
