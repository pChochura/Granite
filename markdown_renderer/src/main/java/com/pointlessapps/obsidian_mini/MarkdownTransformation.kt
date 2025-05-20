package com.pointlessapps.obsidian_mini

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.pointlessapps.obsidian_mini.flavours.obsidian.ObsidianElementTypes
import com.pointlessapps.obsidian_mini.flavours.obsidian.ObsidianFlavourDescriptor
import com.pointlessapps.obsidian_mini.models.NodeMarker
import com.pointlessapps.obsidian_mini.models.NodeProcessorResult
import com.pointlessapps.obsidian_mini.models.NodeStyle
import com.pointlessapps.obsidian_mini.processors.BlockQuoteProcessor
import com.pointlessapps.obsidian_mini.processors.BoldProcessor
import com.pointlessapps.obsidian_mini.processors.CodeSpanProcessor
import com.pointlessapps.obsidian_mini.processors.DefaultProcessor
import com.pointlessapps.obsidian_mini.processors.FootnoteDefinitionProcessor
import com.pointlessapps.obsidian_mini.processors.FootnoteLinkProcessor
import com.pointlessapps.obsidian_mini.processors.HeaderProcessor
import com.pointlessapps.obsidian_mini.processors.HighlightProcessor
import com.pointlessapps.obsidian_mini.processors.InlineLinkProcessor
import com.pointlessapps.obsidian_mini.processors.InternalLinkProcessor
import com.pointlessapps.obsidian_mini.processors.ItalicProcessor
import com.pointlessapps.obsidian_mini.processors.StrikethroughProcessor
import com.pointlessapps.obsidian_mini.providers.BlockQuoteStyleProvider
import com.pointlessapps.obsidian_mini.providers.BoldStyleProvider
import com.pointlessapps.obsidian_mini.providers.CodeSpanStyleProvider
import com.pointlessapps.obsidian_mini.providers.DefaultStyleProvider
import com.pointlessapps.obsidian_mini.providers.FootnoteDefinitionStyleProvider
import com.pointlessapps.obsidian_mini.providers.FootnoteLinkStyleProvider
import com.pointlessapps.obsidian_mini.providers.HeaderStyleProvider
import com.pointlessapps.obsidian_mini.providers.HighlightStyleProvider
import com.pointlessapps.obsidian_mini.providers.InlineLinkStyleProvider
import com.pointlessapps.obsidian_mini.providers.InternalLinkStyleProvider
import com.pointlessapps.obsidian_mini.providers.ItalicStyleProvider
import com.pointlessapps.obsidian_mini.providers.StrikethroughStyleProvider
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.parser.MarkdownParser

class MarkdownTransformation(
    // TODO: Optimize this to omit parsing when moving the cursor
    private val currentCursorPosition: TextRange,
) : VisualTransformation {

    private val parser = MarkdownParser(ObsidianFlavourDescriptor())

    private val footnoteDefinitionProcessor =
        FootnoteDefinitionProcessor(FootnoteDefinitionStyleProvider)
    private val footnoteLinkProcessor = FootnoteLinkProcessor(FootnoteLinkStyleProvider)
    private val highlightProcessor = HighlightProcessor(HighlightStyleProvider)
    private val strikethroughProcessor = StrikethroughProcessor(StrikethroughStyleProvider)
    private val boldProcessor = BoldProcessor(BoldStyleProvider)
    private val italicProcessor = ItalicProcessor(ItalicStyleProvider)
    private val codeSpanProcessor = CodeSpanProcessor(CodeSpanStyleProvider)
    private val blockQuoteProcessor = BlockQuoteProcessor(BlockQuoteStyleProvider)
    private val headerProcessor = HeaderProcessor(HeaderStyleProvider)
    private val inlineLinkProcessor = InlineLinkProcessor(InlineLinkStyleProvider)
    private val internalLinkProcessor = InternalLinkProcessor(InternalLinkStyleProvider)
    private val defaultProcessor = DefaultProcessor(DefaultStyleProvider)

    private fun IElementType.toNodeProcessor(): NodeProcessor = when (this) {
        ObsidianElementTypes.FOOTNOTE_DEFINITION -> footnoteDefinitionProcessor
        ObsidianElementTypes.FOOTNOTE_LINK -> footnoteLinkProcessor
        ObsidianElementTypes.HIGHLIGHT -> highlightProcessor
        GFMElementTypes.STRIKETHROUGH -> strikethroughProcessor
        MarkdownElementTypes.STRONG -> boldProcessor
        MarkdownElementTypes.EMPH -> italicProcessor
        MarkdownElementTypes.CODE_SPAN -> codeSpanProcessor
        MarkdownElementTypes.BLOCK_QUOTE -> blockQuoteProcessor
        MarkdownElementTypes.ATX_1,
        MarkdownElementTypes.ATX_2,
        MarkdownElementTypes.ATX_3,
        MarkdownElementTypes.ATX_4,
        MarkdownElementTypes.ATX_5,
        MarkdownElementTypes.ATX_6,
            -> headerProcessor

        MarkdownElementTypes.INLINE_LINK -> inlineLinkProcessor
        ObsidianElementTypes.INTERNAL_LINK -> internalLinkProcessor
        else -> defaultProcessor
    }

    private fun ASTNode.getStylesAndMarkers(wholeText: String): Pair<List<NodeStyle>, List<NodeMarker>> {
        val styles = mutableListOf<NodeStyle>()
        val markers = mutableListOf<NodeMarker>()

        fun processNode(node: ASTNode) {
            val nodeTextContent = wholeText.substring(node.startOffset, node.endOffset)
            val hideNodeMarkers = if (currentCursorPosition.collapsed) {
                !TextRange(node.startOffset, node.endOffset).contains(currentCursorPosition)
            } else {
                // Show all markers when trying to select the text
                false
            }

            val result = node.type
                .toNodeProcessor()
                .processNode(
                    node = node,
                    textContent = nodeTextContent,
                    hideMarkers = hideNodeMarkers,
                )
            styles.addAll(result.styles)
            markers.addAll(result.markers)
            if (result.processChildren) node.children.forEach(::processNode)
        }

        children.forEach(::processNode)
        return styles to markers.sortedBy { it.startOffset }
    }

    override fun filter(text: AnnotatedString): TransformedText {
        val rootNode = parser.buildMarkdownTreeFromString(text.text)
        val (styles, markers) = rootNode.getStylesAndMarkers(text.text)

        val originalText = text.text
        val transformedTextBuilder = StringBuilder()
        var currentOriginalPos = 0

        // Loop through markers and add text contents between them
        for (marker in markers) {
            if (currentOriginalPos < marker.startOffset) {
                transformedTextBuilder.append(
                    originalText.substring(
                        currentOriginalPos,
                        marker.startOffset,
                    ),
                )
            }
            currentOriginalPos = marker.endOffset
        }

        if (currentOriginalPos < originalText.length) {
            transformedTextBuilder.append(originalText.substring(currentOriginalPos))
        }

        val transformedStyles = styles.mapNotNull { style ->
            val newStart = mapOriginalToTransformed(style.startOffset, markers)
            val newEnd = mapOriginalToTransformed(style.endOffset, markers)
            if (newStart < newEnd) {
                AnnotatedString.Range(style.annotation, newStart, newEnd)
            } else {
                null
            }
        }

        return TransformedText(
            text = AnnotatedString(
                text = transformedTextBuilder.toString(),
                spanStyles = text.spanStyles + transformedStyles
                    .filterIsInstance<AnnotatedString.Range<SpanStyle>>(),
                paragraphStyles = text.paragraphStyles + transformedStyles
                    .filterIsInstance<AnnotatedString.Range<ParagraphStyle>>(),
            ),
            offsetMapping = MarkdownOffsetMapping(markers),
        )
    }

    private fun mapOriginalToTransformed(originalOffset: Int, markers: List<NodeMarker>): Int {
        var removedLength = 0
        for (marker in markers) {
            if (originalOffset <= marker.startOffset) {
                break
            }

            if (originalOffset < marker.endOffset) {
                return marker.startOffset - removedLength
            }

            removedLength += marker.endOffset - marker.startOffset
        }

        return originalOffset - removedLength
    }
}
