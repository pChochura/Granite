package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.StringAnnotation
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.em
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMapNotNull
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.models.ChildrenProcessing
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.spans.BlockQuoteMarkdownSpanStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.spans.CalloutMarkdownSpanStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.utils.CalloutTypes
import com.pointlessapps.obsidian_mini.markdown.renderer.utils.atLineEnd
import com.pointlessapps.obsidian_mini.markdown.renderer.utils.atLineStart
import com.pointlessapps.obsidian_mini.markdown.renderer.utils.capitalize
import com.pointlessapps.obsidian_mini.markdown.renderer.utils.getIndentRegions
import com.pointlessapps.obsidian_mini.markdown.renderer.utils.withRange
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode

internal object BlockQuoteProcessor : NodeProcessor {

    private val markerRegex = Regex("^(?: {0,3}> ?)*")
    private val calloutRegex = Regex("^ {0,3}> ?(\\[!(.*)](?: +|$))(.*)$")

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
        val firstLine = nodeTextContent.lineSequence().first()
        val calloutMatch = calloutRegex.find(firstLine)
        val markers = extractMarkers(nodeTextContent).fastMap {
            NodeMarker(
                startOffset = it.startOffset + node.startOffset,
                endOffset = it.endOffset + node.startOffset,
            )
        }

        if (calloutMatch != null) {
            return markers + NodeMarker(
                startOffset = node.startOffset + calloutMatch.groups[1]!!.range.first,
                endOffset = node.startOffset + calloutMatch.groups[1]!!.range.last + 1,
                replacement = "\t\t\t" + extractCalloutTitle(calloutMatch).orEmpty().capitalize(),
            )
        }

        return markers
    }

    override fun processStyles(
        node: ASTNode,
        textContent: String,
    ): List<AnnotatedString.Range<AnnotatedString.Annotation>> {
        val nodeTextContent = textContent.substring(node.startOffset, node.endOffset)
        val openingMarkers = extractMarkers(nodeTextContent)

        if (openingMarkers.isEmpty()) {
            return emptyList()
        }

        val firstLine = nodeTextContent.lineSequence().first()
        val calloutMatch = calloutRegex.find(firstLine)
        return if (calloutMatch != null) {
            getCalloutStyles(node, textContent, openingMarkers, calloutMatch)
        } else {
            getBlockQuoteStyles(node, textContent, openingMarkers)
        }
    }

    private fun getCalloutStyles(
        node: ASTNode,
        textContent: String,
        openingMarkers: List<OpeningMarker>,
        calloutMatch: MatchResult,
    ): List<AnnotatedString.Range<AnnotatedString.Annotation>> {
        val lines = textContent.substring(node.startOffset, node.endOffset).lines()
        val linesOffsets = lines.runningFold(node.startOffset) { length, line ->
            length + line.length + 1
        }
        val indentRanges = getIndentRegions(openingMarkers.fastMap { it.indent })
            .fastFilter { it.second > 0 }

        val paragraphStyle = ParagraphStyle(
            textIndent = TextIndent(
                firstLine = 1.em,
                restLine = 1.em,
            ),
            lineHeight = 1.6.em,
            lineHeightStyle = LineHeightStyle.Default,
        )

        return listOf(
            paragraphStyle.withRange(
                start = node.startOffset.atLineStart(textContent),
                // Add an additional offset to make the paragraph render smoother
                end = node.endOffset.atLineEnd(textContent) + 1,
            ),
            SpanStyle(
                fontWeight = FontWeight.Bold,
                color = CalloutTypes.getColor(calloutMatch.groups[2]!!.value),
            ).withRange(
                start = node.startOffset + calloutMatch.groups[1]!!.range.first,
                end = node.startOffset.atLineEnd(textContent),
            ),
            StringAnnotation(calloutMatch.groups[2]!!.value).withRange(
                start = node.startOffset + calloutMatch.groups[1]!!.range.first,
                end = node.startOffset.atLineEnd(textContent),
                tag = CalloutMarkdownSpanStyle.TAG_LABEL,
            ),
            SpanStyle().withRange(
                start = node.startOffset,
                end = node.endOffset,
                tag = CalloutMarkdownSpanStyle.TAG_CONTENT,
            )
        ) + openingMarkers.fastMap {
            SpanStyle(color = Color.DarkGray).withRange(
                start = it.startOffset + node.startOffset,
                end = it.endOffset + node.startOffset,
            )
        } + indentRanges.fastFlatMap { (range, indent) ->
            listOf(
                paragraphStyle.copy(
                    textIndent = TextIndent(
                        firstLine = (indent + 1).em,
                        restLine = (indent + 1).em,
                    )
                ).withRange(
                    start = linesOffsets[range.first].atLineStart(textContent),
                    // Add an additional offset to make the paragraph render smoother
                    end = linesOffsets[range.last].atLineEnd(textContent) + 1,
                ),
                StringAnnotation(indent.toString()).withRange(
                    start = linesOffsets[range.first].atLineStart(textContent),
                    end = linesOffsets[range.last].atLineEnd(textContent),
                    tag = CalloutMarkdownSpanStyle.TAG_INDENT,
                ),
            )
        }
    }

    private fun getBlockQuoteStyles(
        node: ASTNode,
        textContent: String,
        openingMarkers: List<OpeningMarker>,
    ): List<AnnotatedString.Range<AnnotatedString.Annotation>> {
        val lines = textContent.substring(node.startOffset, node.endOffset).lines()
        val linesOffsets = lines.runningFold(node.startOffset) { length, line ->
            length + line.length + 1
        }
        val indentRanges = getIndentRegions(openingMarkers.fastMap { it.indent })

        val paragraphStyle = ParagraphStyle(
            textIndent = TextIndent(
                firstLine = 1.em,
                restLine = 1.em,
            ),
            lineHeight = 1.6.em,
            lineHeightStyle = LineHeightStyle.Default,
        )

        return listOf(
            paragraphStyle.withRange(
                start = node.startOffset.atLineStart(textContent),
                // Add an additional offset to make the paragraph render smoother
                end = node.endOffset.atLineEnd(textContent) + 1,
            ),
            SpanStyle(fontStyle = FontStyle.Italic, color = Color.Cyan).withRange(
                start = node.startOffset,
                end = node.endOffset,
                tag = BlockQuoteMarkdownSpanStyle.TAG_CONTENT,
            )
        ) + openingMarkers.fastMap {
            SpanStyle(fontStyle = FontStyle.Italic, color = Color.DarkGray).withRange(
                start = it.startOffset + node.startOffset,
                end = it.endOffset + node.startOffset,
            )
        } + indentRanges.fastFlatMap { (range, indent) ->
            listOf(
                paragraphStyle.copy(
                    textIndent = TextIndent(
                        firstLine = (indent + 1).em,
                        restLine = (indent + 1).em,
                    )
                ).withRange(
                    start = linesOffsets[range.first].atLineStart(textContent),
                    // Add an additional offset to make the paragraph render smoother
                    end = linesOffsets[range.last].atLineEnd(textContent) + 1,
                ),
                StringAnnotation(indent.toString()).withRange(
                    start = linesOffsets[range.first].atLineStart(textContent),
                    end = linesOffsets[range.last].atLineEnd(textContent),
                    tag = BlockQuoteMarkdownSpanStyle.TAG_INDENT,
                ),
            )
        }
    }

    override fun processMarkers(node: ASTNode) =
        throw IllegalStateException("Could not process markers for the blockquote without the text content")

    override fun processStyles(node: ASTNode) =
        throw IllegalStateException("Could not process styles for the blockquote without the text content")

    override fun processChild(type: IElementType) =
        if (type == MarkdownElementTypes.BLOCK_QUOTE) {
            ChildrenProcessing.SKIP_PARENT
        } else {
            ChildrenProcessing.PROCESS_CHILDREN
        }
}
