package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastMapNotNull
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import com.pointlessapps.obsidian_mini.markdown.renderer.models.toNodeStyles
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode

internal class BlockQuoteProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    private companion object {
        val markerRegex = Regex("^(?: {0,3}> ?)*")
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
                    indent = group.value.count { it == '>' }.coerceAtLeast(1),
                )
            }
            // Add a line break
            currentIndex += line.length + 1

            return@fastMapNotNull marker
        }
    }

    override fun processMarkers(node: ASTNode, textContent: String) =
        extractMarkers(textContent.substring(node.startOffset, node.endOffset)).map {
            NodeMarker(
                startOffset = it.startOffset + node.startOffset,
                endOffset = it.endOffset + node.startOffset,
                replacement = "\t".repeat(it.indent),
            )
        }

    override fun processStyles(node: ASTNode, textContent: String): List<NodeStyle> {
        val openingMarkers = extractMarkers(textContent.substring(node.startOffset, node.endOffset))

        if (openingMarkers.isEmpty()) {
            return emptyList()
        }

        return styleProvider.styleNodeElement(NodeType.Paragraph, node.type).toNodeStyles(
            startOffset = node.startOffset,
            // Add an additional offset to make the paragraph render smoother
            endOffset = node.endOffset + 1,
        ) + styleProvider.styleNodeElement(NodeType.All, node.type).toNodeStyles(
            startOffset = node.startOffset,
            endOffset = node.endOffset,
        ) + openingMarkers.fastFlatMap {
            styleProvider.styleNodeElement(NodeType.Decoration, node.type).toNodeStyles(
                startOffset = it.startOffset + node.startOffset,
                endOffset = it.endOffset + node.startOffset,
            )
        }
    }

    override fun processMarkers(node: ASTNode) =
        throw IllegalStateException("Could not process markers for the blockquote without the text content")

    override fun processStyles(node: ASTNode) =
        throw IllegalStateException("Could not process styles for the blockquote without the text content")

    override fun shouldProcessChild(type: IElementType) = type != MarkdownElementTypes.BLOCK_QUOTE
}
