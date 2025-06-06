package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMapNotNull
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.atLineEnd
import com.pointlessapps.obsidian_mini.markdown.renderer.atLineStart
import com.pointlessapps.obsidian_mini.markdown.renderer.capitalize
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import com.pointlessapps.obsidian_mini.markdown.renderer.models.toNodeStyles
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.spans.BlockQuoteMarkdownSpanStyle
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode

internal class BlockQuoteProcessor(
    private val blockQuoteStyleProvider: ProcessorStyleProvider,
    private val calloutStyleProvider: ProcessorStyleProvider,
) : NodeProcessor {

    private companion object {
        val markerRegex = Regex("^(?: {0,3}> ?)*")
        val calloutRegex = Regex("^ {0,3}> ?(\\[!([^]]+)](?: +|$))(.*)$", RegexOption.MULTILINE)
    }

    private data class OpeningMarker(
        val startOffset: Int,
        val endOffset: Int,
        val indent: Int,
    )

    private fun extractMarkers(nodeTextContent: String): List<OpeningMarker> {
        var currentIndex = 0
        return nodeTextContent.lines().fastMapNotNull { line ->
            val matchedGroup = markerRegex.find(line)?.groups?.firstOrNull()
            val marker = matchedGroup?.let { group ->
                OpeningMarker(
                    startOffset = currentIndex + group.range.first,
                    endOffset = currentIndex + group.range.last + 1,
                    indent = group.value.count { it == '>' }.coerceAtLeast(1) - 1,
                )
            }
            // Add a line break
            currentIndex += line.length + 1

            return@fastMapNotNull marker
        }
    }

    private fun extractCalloutTitle(calloutMatch: MatchResult) =
        if (calloutMatch.groups[3]!!.value.isEmpty()) {
            calloutMatch.groups[2]?.value
        } else {
            null
        }

    override fun processMarkers(node: ASTNode, textContent: String): List<NodeMarker> {
        val nodeTextContent = textContent.substring(node.startOffset, node.endOffset)
        val calloutMatch = calloutRegex.find(nodeTextContent)
        val markers = extractMarkers(nodeTextContent).fastMap {
            NodeMarker(
                startOffset = it.startOffset + node.startOffset,
                endOffset = it.endOffset + node.startOffset,
                replacement = "\t\t".repeat(it.indent),
            )
        }

        if (calloutMatch != null) {
            return markers + NodeMarker(
                startOffset = node.startOffset + calloutMatch.groups[1]!!.range.first,
                endOffset = node.startOffset + calloutMatch.groups[1]!!.range.last + 1,
                replacement = "    " + extractCalloutTitle(calloutMatch).orEmpty().capitalize(),
            )
        }

        return markers
    }

    override fun processStyles(node: ASTNode, textContent: String): List<NodeStyle> {
        val nodeTextContent = textContent.substring(node.startOffset, node.endOffset)
        val openingMarkers = extractMarkers(nodeTextContent)

        if (openingMarkers.isEmpty()) {
            return emptyList()
        }

        val openingMarkersStyles = openingMarkers.fastFlatMap {
            blockQuoteStyleProvider.styleNodeElement(NodeType.Decoration, node.type).toNodeStyles(
                startOffset = it.startOffset + node.startOffset,
                endOffset = it.endOffset + node.startOffset,
            )
        }

        val calloutMatch = calloutRegex.find(nodeTextContent)
        return if (calloutMatch != null) {
            blockQuoteStyleProvider.styleNodeElement(NodeType.Paragraph, node.type).toNodeStyles(
                startOffset = node.startOffset.atLineStart(textContent),
                // Add an additional offset to make the paragraph render smoother
                endOffset = node.endOffset.atLineEnd(textContent) + 1,
            ) + calloutStyleProvider.styleNodeElement(NodeType.Label, node.type).toNodeStyles(
                startOffset = node.startOffset.atLineStart(textContent),
                endOffset = node.startOffset.atLineEnd(textContent),
            ) + calloutStyleProvider.styleNodeElement(
                element = NodeType.Data(calloutMatch.groups[2]!!.value),
                type = node.type,
            ).toNodeStyles(
                startOffset = node.startOffset.atLineStart(textContent),
                endOffset = node.startOffset.atLineEnd(textContent),
            ) + openingMarkersStyles
        } else {
            blockQuoteStyleProvider.styleNodeElement(NodeType.Paragraph, node.type).toNodeStyles(
                startOffset = node.startOffset.atLineStart(textContent),
                // Add an additional offset to make the paragraph render smoother
                endOffset = node.endOffset.atLineEnd(textContent) + 1,
            ) + blockQuoteStyleProvider.styleNodeElement(NodeType.All, node.type).toNodeStyles(
                startOffset = node.startOffset,
                endOffset = node.endOffset,
                tag = BlockQuoteMarkdownSpanStyle.TAG_CONTENT,
            ) + openingMarkersStyles
        }
    }

    override fun processMarkers(node: ASTNode) =
        throw IllegalStateException("Could not process markers for the blockquote without the text content")

    override fun processStyles(node: ASTNode) =
        throw IllegalStateException("Could not process styles for the blockquote without the text content")

    override fun shouldProcessChild(type: IElementType) = type != MarkdownElementTypes.BLOCK_QUOTE
}
