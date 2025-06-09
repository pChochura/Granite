package com.pointlessapps.granite.markdown.renderer

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.util.fastForEachReversed
import androidx.compose.ui.util.fastMapNotNull
import com.pointlessapps.markdown.granite.parser.obsidian.ObsidianElementTypes
import com.pointlessapps.markdown.granite.parser.obsidian.ObsidianFlavourDescriptor
import com.pointlessapps.granite.markdown.renderer.models.AccumulateStylesResult
import com.pointlessapps.granite.markdown.renderer.models.ChildrenProcessing
import com.pointlessapps.granite.markdown.renderer.models.MarkdownParsingResult
import com.pointlessapps.granite.markdown.renderer.models.NodeMarker
import com.pointlessapps.granite.markdown.renderer.processors.BlockIdProcessor
import com.pointlessapps.granite.markdown.renderer.processors.BlockQuoteProcessor
import com.pointlessapps.granite.markdown.renderer.processors.BoldProcessor
import com.pointlessapps.granite.markdown.renderer.processors.CodeBlockProcessor
import com.pointlessapps.granite.markdown.renderer.processors.CodeSpanProcessor
import com.pointlessapps.granite.markdown.renderer.processors.CommentBlockProcessor
import com.pointlessapps.granite.markdown.renderer.processors.CommentProcessor
import com.pointlessapps.granite.markdown.renderer.processors.DefaultProcessor
import com.pointlessapps.granite.markdown.renderer.processors.EmbedProcessor
import com.pointlessapps.granite.markdown.renderer.processors.FootnoteDefinitionProcessor
import com.pointlessapps.granite.markdown.renderer.processors.FootnoteLinkProcessor
import com.pointlessapps.granite.markdown.renderer.processors.HashtagProcessor
import com.pointlessapps.granite.markdown.renderer.processors.HeaderProcessor
import com.pointlessapps.granite.markdown.renderer.processors.HighlightProcessor
import com.pointlessapps.granite.markdown.renderer.processors.HorizontalRuleProcessor
import com.pointlessapps.granite.markdown.renderer.processors.ImageProcessor
import com.pointlessapps.granite.markdown.renderer.processors.InlineFootnoteProcessor
import com.pointlessapps.granite.markdown.renderer.processors.InlineLinkProcessor
import com.pointlessapps.granite.markdown.renderer.processors.InternalLinkProcessor
import com.pointlessapps.granite.markdown.renderer.processors.ItalicProcessor
import com.pointlessapps.granite.markdown.renderer.processors.OrderedListProcessor
import com.pointlessapps.granite.markdown.renderer.processors.StrikethroughProcessor
import com.pointlessapps.granite.markdown.renderer.processors.UnorderedListProcessor
import com.pointlessapps.granite.markdown.renderer.utils.buildAnnotatedString
import com.pointlessapps.granite.markdown.renderer.utils.processNode
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.parser.MarkdownParser
import kotlin.math.max
import kotlin.math.min

class MarkdownTransformation(private var currentCursorPosition: TextRange) : VisualTransformation {

    private var markdownParsingResult: MarkdownParsingResult? = null

    private val parser = MarkdownParser(ObsidianFlavourDescriptor())
    private val linkInteractionListener: LinkInteractionListener? = null

    private val processors: Map<IElementType, NodeProcessor> = mapOf(
        ObsidianElementTypes.BLOCK_ID to BlockIdProcessor,
        ObsidianElementTypes.HASHTAG to HashtagProcessor,
        ObsidianElementTypes.FOOTNOTE_DEFINITION to FootnoteDefinitionProcessor,
        ObsidianElementTypes.FOOTNOTE_LINK to FootnoteLinkProcessor,
        ObsidianElementTypes.INLINE_FOOTNOTE to InlineFootnoteProcessor,
        ObsidianElementTypes.HIGHLIGHT to HighlightProcessor,
        ObsidianElementTypes.COMMENT to CommentProcessor,
        ObsidianElementTypes.COMMENT_BLOCK to CommentBlockProcessor,
        ObsidianElementTypes.INTERNAL_LINK to InternalLinkProcessor,
        ObsidianElementTypes.EMBED to EmbedProcessor,
        GFMElementTypes.STRIKETHROUGH to StrikethroughProcessor,
        MarkdownTokenTypes.HORIZONTAL_RULE to HorizontalRuleProcessor,
        MarkdownElementTypes.UNORDERED_LIST to UnorderedListProcessor,
        MarkdownElementTypes.ORDERED_LIST to OrderedListProcessor,
        MarkdownElementTypes.STRONG to BoldProcessor,
        MarkdownElementTypes.EMPH to ItalicProcessor,
        MarkdownElementTypes.CODE_SPAN to CodeSpanProcessor,
        MarkdownElementTypes.CODE_FENCE to CodeBlockProcessor,
        MarkdownElementTypes.BLOCK_QUOTE to BlockQuoteProcessor,
        MarkdownElementTypes.IMAGE to ImageProcessor,
        MarkdownElementTypes.INLINE_LINK to InlineLinkProcessor(linkInteractionListener),
        MarkdownElementTypes.ATX_1 to HeaderProcessor,
        MarkdownElementTypes.ATX_2 to HeaderProcessor,
        MarkdownElementTypes.ATX_3 to HeaderProcessor,
        MarkdownElementTypes.ATX_4 to HeaderProcessor,
        MarkdownElementTypes.ATX_5 to HeaderProcessor,
        MarkdownElementTypes.ATX_6 to HeaderProcessor,
    )

    private fun getMarkdownParsingResult(text: String): MarkdownParsingResult {
        if (markdownParsingResult == null || markdownParsingResult?.textContent != text) {
            markdownParsingResult = MarkdownParsingResult(
                rootNode = parser.buildMarkdownTreeFromString(text),
                textContent = text,
            )
        }

        return markdownParsingResult!!
    }

    private fun shouldHideMarkers(node: ASTNode, cursorPosition: TextRange) =
        if (cursorPosition.collapsed) {
            !TextRange(node.startOffset, node.endOffset).contains(cursorPosition)
        } else {
            // Show all markers when trying to select the text
            false
        }

    private fun ASTNode.accumulateStyles(
        cursorPosition: TextRange,
        textContent: String,
    ): AccumulateStylesResult {
        val styles = mutableListOf<AnnotatedString.Range<AnnotatedString.Annotation>>()
        val markers = mutableListOf<NodeMarker>()
        val nodeStack = ArrayDeque<ASTNode>()

        children.fastForEachReversed { nodeStack.addLast(it) }

        fun ASTNode.processChildren(nodeProcessor: NodeProcessor) {
            children.fastForEachReversed { childNode ->
                when (nodeProcessor.processChild(childNode.type)) {
                    ChildrenProcessing.PROCESS_CHILDREN -> nodeStack.addLast(childNode)
                    ChildrenProcessing.SKIP_PARENT -> childNode.processChildren(nodeProcessor)
                    ChildrenProcessing.SKIP -> {} // Skip
                }
            }
        }

        while (nodeStack.isNotEmpty()) {
            val currentNode = nodeStack.removeLast()
            val nodeProcessor = processors[currentNode.type] ?: DefaultProcessor
            val hideNodeMarkers = shouldHideMarkers(currentNode, cursorPosition)

            val processingResult = nodeProcessor.processNode(
                node = currentNode,
                textContent = textContent,
                hideMarkers = hideNodeMarkers,
            )

            styles.addAll(processingResult.styles)
            markers.addAll(processingResult.markers)
            currentNode.processChildren(nodeProcessor)
        }

        return AccumulateStylesResult(
            styles = styles,
            markers = markers.sortedBy { it.startOffset },
        )
    }

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        val transformedTextBuilder = StringBuilder()
        var lastMarkerPos = 0
        val result = getMarkdownParsingResult(originalText)
            .rootNode.accumulateStyles(currentCursorPosition, originalText)

        // Loop through markers and add text contents between them
        for (marker in result.markers) {
            if (lastMarkerPos < marker.startOffset) {
                transformedTextBuilder.append(
                    originalText.substring(
                        lastMarkerPos,
                        marker.startOffset,
                    ),
                )
            }

            if (marker.replacement != null) {
                transformedTextBuilder.append(marker.replacement)
            }
            lastMarkerPos = marker.endOffset
        }

        if (lastMarkerPos < originalText.length) {
            transformedTextBuilder.append(originalText.substring(lastMarkerPos))
        }

        val offsetMapping = MarkdownOffsetMapping(result.markers, originalText.length)
        val transformedStyles = result.styles.fastMapNotNull { style ->
            val newStart = offsetMapping.originalToTransformed(style.start)
            val newEnd = offsetMapping.originalToTransformed(style.end)
            if (newStart < newEnd) {
                AnnotatedString.Range(
                    item = style.item,
                    start = max(0, newStart),
                    end = min(newEnd, transformedTextBuilder.length),
                    tag = style.tag,
                )
            } else null
        }

        return TransformedText(
            text = buildAnnotatedString(transformedTextBuilder.toString(), transformedStyles),
            offsetMapping = offsetMapping,
        )
    }

    fun withSelection(selection: TextRange): MarkdownTransformation {
        currentCursorPosition = selection

        return this
    }
}
