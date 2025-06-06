package com.pointlessapps.obsidian_mini.markdown.renderer

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.StringAnnotation
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMapNotNull
import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianElementTypes
import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianFlavourDescriptor
import com.pointlessapps.obsidian_mini.markdown.renderer.models.MarkdownParsingResult
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.BlockIdProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.BlockQuoteProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.BoldProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.CodeBlockProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.CodeSpanProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.CommentBlockProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.CommentProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.DefaultProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.EmbedProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.FootnoteDefinitionProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.FootnoteLinkProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.HashtagProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.HeaderProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.HighlightProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.HorizontalRuleProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.ImageProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.InlineFootnoteProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.InlineLinkProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.InternalLinkProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.ItalicProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.processors.StrikethroughProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.BlockIdStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.BlockQuoteStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.BoldStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.CodeBlockStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.CodeSpanStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.CommentBlockStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.CommentStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.EmbedStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.FootnoteDefinitionStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.FootnoteLinkStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.HashtagStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.HeaderStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.HighlightStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.HorizontalRulStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.ImageStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.InlineFootnoteStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.InlineLinkStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.InternalLinkStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.ItalicStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.StrikethroughStyleProvider
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.parser.MarkdownParser
import kotlin.math.max
import kotlin.math.min

class MarkdownTransformation(private var currentCursorPosition: TextRange) : VisualTransformation {

    private data class AccumulateStylesResult(
        val styles: List<NodeStyle>,
        val markers: List<NodeMarker>,
    )

    private val parser = MarkdownParser(ObsidianFlavourDescriptor())
    private var markdownParsingResult: MarkdownParsingResult? = null

    private val processors = mapOf(
        ObsidianElementTypes.BLOCK_ID to BlockIdProcessor(BlockIdStyleProvider),
        ObsidianElementTypes.HASHTAG to HashtagProcessor(HashtagStyleProvider),
        ObsidianElementTypes.FOOTNOTE_DEFINITION to FootnoteDefinitionProcessor(
            FootnoteDefinitionStyleProvider,
        ),
        ObsidianElementTypes.FOOTNOTE_LINK to FootnoteLinkProcessor(FootnoteLinkStyleProvider),
        ObsidianElementTypes.INLINE_FOOTNOTE to InlineFootnoteProcessor(InlineFootnoteStyleProvider),
        ObsidianElementTypes.HIGHLIGHT to HighlightProcessor(HighlightStyleProvider),
        ObsidianElementTypes.COMMENT to CommentProcessor(CommentStyleProvider),
        ObsidianElementTypes.COMMENT_BLOCK to CommentBlockProcessor(CommentBlockStyleProvider),
        ObsidianElementTypes.INTERNAL_LINK to InternalLinkProcessor(InternalLinkStyleProvider),
        ObsidianElementTypes.EMBED to EmbedProcessor(EmbedStyleProvider),
        GFMElementTypes.STRIKETHROUGH to StrikethroughProcessor(StrikethroughStyleProvider),
        MarkdownTokenTypes.HORIZONTAL_RULE to HorizontalRuleProcessor(HorizontalRulStyleProvider),
        MarkdownElementTypes.STRONG to BoldProcessor(BoldStyleProvider),
        MarkdownElementTypes.EMPH to ItalicProcessor(ItalicStyleProvider),
        MarkdownElementTypes.CODE_SPAN to CodeSpanProcessor(CodeSpanStyleProvider),
        MarkdownElementTypes.CODE_FENCE to CodeBlockProcessor(CodeBlockStyleProvider),
        MarkdownElementTypes.BLOCK_QUOTE to BlockQuoteProcessor(BlockQuoteStyleProvider),
        MarkdownElementTypes.IMAGE to ImageProcessor(ImageStyleProvider),
        MarkdownElementTypes.INLINE_LINK to InlineLinkProcessor(InlineLinkStyleProvider),
        MarkdownElementTypes.ATX_1 to HeaderProcessor(HeaderStyleProvider),
        MarkdownElementTypes.ATX_2 to HeaderProcessor(HeaderStyleProvider),
        MarkdownElementTypes.ATX_3 to HeaderProcessor(HeaderStyleProvider),
        MarkdownElementTypes.ATX_4 to HeaderProcessor(HeaderStyleProvider),
        MarkdownElementTypes.ATX_5 to HeaderProcessor(HeaderStyleProvider),
        MarkdownElementTypes.ATX_6 to HeaderProcessor(HeaderStyleProvider),
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

    private fun ASTNode.accumulateStyles(
        cursorPosition: TextRange,
        textContent: String,
    ): AccumulateStylesResult {
        val styles = mutableListOf<NodeStyle>()
        val markers = mutableListOf<NodeMarker>()

        fun processNode(node: ASTNode) {
            val hideNodeMarkers = if (cursorPosition.collapsed) {
                !TextRange(node.startOffset, node.endOffset).contains(cursorPosition)
            } else {
                // Show all markers when trying to select the text
                false
            }

            val nodeProcessor = processors[node.type] ?: DefaultProcessor
            val result = nodeProcessor.processNode(node, hideNodeMarkers, textContent)

            styles.addAll(result.styles)
            markers.addAll(result.markers)
            node.children.fastForEach {
                if (nodeProcessor.shouldProcessChild(it.type)) {
                    processNode(it)
                }
            }
        }

        children.fastForEach(::processNode)
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
            val newStart = offsetMapping.originalToTransformed(style.startOffset)
            val newEnd = offsetMapping.originalToTransformed(style.endOffset)
            if (newStart < newEnd) {
                AnnotatedString.Range(
                    item = style.annotation,
                    start = max(0, newStart),
                    end = min(newEnd, transformedTextBuilder.length),
                    tag = style.tag.orEmpty(),
                )
            } else null
        }

        return TransformedText(
            text = buildAnnotatedString {
                append(transformedTextBuilder)
                transformedStyles.fastForEach {
                    when (val item = it.item) {
                        is SpanStyle -> addStyle(item, it.start, it.end)
                        is ParagraphStyle -> addStyle(item, it.start, it.end)
                        is StringAnnotation -> addStringAnnotation(
                            tag = it.tag,
                            annotation = item.value,
                            start = it.start,
                            end = it.end,
                        )

                        else -> {} // TODO cover different cases
                    }
                }
            },
            offsetMapping = offsetMapping,
        )
    }

    fun withSelection(selection: TextRange): MarkdownTransformation {
        currentCursorPosition = selection

        return this
    }
}
