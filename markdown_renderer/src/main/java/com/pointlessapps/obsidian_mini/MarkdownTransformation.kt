package com.pointlessapps.obsidian_mini

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.pointlessapps.obsidian_mini.flavours.obsidian.ObsidianElementTypes
import com.pointlessapps.obsidian_mini.flavours.obsidian.ObsidianFlavourDescriptor
import com.pointlessapps.obsidian_mini.processors.BlockQuoteProcessor
import com.pointlessapps.obsidian_mini.processors.BoldProcessor
import com.pointlessapps.obsidian_mini.processors.CodeSpanProcessor
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
import com.pointlessapps.obsidian_mini.providers.FootnoteDefinitionStyleProvider
import com.pointlessapps.obsidian_mini.providers.FootnoteLinkStyleProvider
import com.pointlessapps.obsidian_mini.providers.HeaderStyleProvider
import com.pointlessapps.obsidian_mini.providers.HighlightStyleProvider
import com.pointlessapps.obsidian_mini.providers.InlineLinkStyleProvider
import com.pointlessapps.obsidian_mini.providers.InternalLinkStyleProvider
import com.pointlessapps.obsidian_mini.providers.ItalicStyleProvider
import com.pointlessapps.obsidian_mini.providers.StrikethroughStyleProvider
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.parser.MarkdownParser

class MarkdownTransformation(
    private val activeBlockOriginalRange: TextRange?,
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

    private fun ASTNode.text(markdownText: String) = markdownText.substring(startOffset, endOffset)

    // Modified to return styles and marker info
    private fun List<ASTNode>.getStylesAndMarkers(wholeText: String): Pair<List<NodeStyle>, List<NodeMarker>> {
        val styles = mutableListOf<NodeStyle>()
        val markers = mutableListOf<NodeMarker>()

        fun processNode(node: ASTNode) {
            val nodeTextContent = node.text(wholeText)
            val isNodeActive = activeBlockOriginalRange?.let {
                // TODO Fix when the selection isn't collapsed
                !TextRange(node.startOffset, node.endOffset).contains(it)
            } ?: true

            when (node.type) {
                MarkdownElementTypes.PARAGRAPH -> node.children.forEach(::processNode)

                ObsidianElementTypes.FOOTNOTE_DEFINITION -> {
                    if (isNodeActive) {
                        markers.addAll(
                            footnoteDefinitionProcessor.processMarkers(node, nodeTextContent),
                        )
                    }
                    styles.addAll(footnoteDefinitionProcessor.processStyles(node, nodeTextContent))

                    if (footnoteDefinitionProcessor.shouldProcessChildren()) {
                        node.children.forEach(::processNode)
                    }
                }

                ObsidianElementTypes.FOOTNOTE_LINK -> {
                    if (isNodeActive) {
                        markers.addAll(footnoteLinkProcessor.processMarkers(node, nodeTextContent))
                    }
                    styles.addAll(footnoteLinkProcessor.processStyles(node, nodeTextContent))

                    if (footnoteLinkProcessor.shouldProcessChildren()) {
                        node.children.forEach(::processNode)
                    }
                }

                ObsidianElementTypes.HIGHLIGHT -> {
                    if (isNodeActive) {
                        markers.addAll(highlightProcessor.processMarkers(node, nodeTextContent))
                    }
                    styles.addAll(highlightProcessor.processStyles(node, nodeTextContent))

                    if (highlightProcessor.shouldProcessChildren()) {
                        node.children.forEach(::processNode)
                    }
                }

                GFMElementTypes.STRIKETHROUGH -> {
                    if (isNodeActive) {
                        markers.addAll(strikethroughProcessor.processMarkers(node, nodeTextContent))
                    }
                    styles.addAll(strikethroughProcessor.processStyles(node, nodeTextContent))

                    if (strikethroughProcessor.shouldProcessChildren()) {
                        node.children.forEach(::processNode)
                    }
                }

                MarkdownElementTypes.STRONG -> {
                    if (isNodeActive) {
                        markers.addAll(boldProcessor.processMarkers(node, nodeTextContent))
                    }
                    styles.addAll(boldProcessor.processStyles(node, nodeTextContent))

                    if (boldProcessor.shouldProcessChildren()) {
                        node.children.forEach(::processNode)
                    }
                }

                MarkdownElementTypes.EMPH -> {
                    if (isNodeActive) {
                        markers.addAll(italicProcessor.processMarkers(node, nodeTextContent))
                    }
                    styles.addAll(italicProcessor.processStyles(node, nodeTextContent))

                    if (italicProcessor.shouldProcessChildren()) {
                        node.children.forEach(::processNode)
                    }
                }

                MarkdownElementTypes.CODE_SPAN -> {
                    if (isNodeActive) {
                        markers.addAll(codeSpanProcessor.processMarkers(node, nodeTextContent))
                    }
                    styles.addAll(codeSpanProcessor.processStyles(node, nodeTextContent))

                    if (codeSpanProcessor.shouldProcessChildren()) {
                        node.children.forEach(::processNode)
                    }
                }

                MarkdownElementTypes.BLOCK_QUOTE -> {
                    if (isNodeActive) {
                        markers.addAll(blockQuoteProcessor.processMarkers(node, nodeTextContent))
                    }
                    styles.addAll(blockQuoteProcessor.processStyles(node, nodeTextContent))

                    if (blockQuoteProcessor.shouldProcessChildren()) {
                        node.children.forEach(::processNode)
                    }
                }

                MarkdownElementTypes.ATX_1,
                MarkdownElementTypes.ATX_2,
                MarkdownElementTypes.ATX_3,
                MarkdownElementTypes.ATX_4,
                MarkdownElementTypes.ATX_5,
                MarkdownElementTypes.ATX_6,
                    -> {
                    if (isNodeActive) {
                        markers.addAll(headerProcessor.processMarkers(node, nodeTextContent))
                    }
                    styles.addAll(headerProcessor.processStyles(node, nodeTextContent))

                    if (headerProcessor.shouldProcessChildren()) {
                        node.children.forEach(::processNode)
                    }
                }

                MarkdownElementTypes.INLINE_LINK -> {
                    if (isNodeActive) {
                        markers.addAll(inlineLinkProcessor.processMarkers(node, nodeTextContent))
                    }
                    styles.addAll(inlineLinkProcessor.processStyles(node, nodeTextContent))

                    if (inlineLinkProcessor.shouldProcessChildren()) {
                        node.children.forEach(::processNode)
                    }
                }

                ObsidianElementTypes.INTERNAL_LINK -> {
                    if (isNodeActive) {
                        markers.addAll(internalLinkProcessor.processMarkers(node, nodeTextContent))
                    }
                    styles.addAll(internalLinkProcessor.processStyles(node, nodeTextContent))

                    if (internalLinkProcessor.shouldProcessChildren()) {
                        node.children.forEach(::processNode)
                    }
                }

                else -> node.children.forEach(::processNode)
            }
        }

        this.forEach(::processNode)
        return styles to markers.sortedBy { it.startOffset } // Important to sort for OffsetMapping
    }

    override fun filter(text: AnnotatedString): TransformedText {
        val astRoot = parser.buildMarkdownTreeFromString(text.text)
        val (styles, markers) = astRoot.children.getStylesAndMarkers(text.text)

        // 1. Build the transformed string by removing markers
        val originalText = text.text
        val transformedTextBuilder = StringBuilder()
        var currentOriginalPos = 0
        for (marker in markers) {
            if (currentOriginalPos < marker.startOffset) {
                transformedTextBuilder.append(
                    originalText.substring(
                        currentOriginalPos,
                        marker.startOffset
                    )
                )
            }
            // Content within the marker (if any, usually none for simple markers)
            // transformedTextBuilder.append(originalText.substring(marker.originalStart, marker.originalEnd).take(marker.transformedLength))
            currentOriginalPos = marker.endOffset
        }
        if (currentOriginalPos < originalText.length) {
            transformedTextBuilder.append(originalText.substring(currentOriginalPos))
        }
        val transformedString = transformedTextBuilder.toString()

        // 2. Adjust style ranges for the transformed string
        val transformedStyles = styles.mapNotNull { style ->
            val newStart = mapOriginalToTransformed(style.startOffset, markers)
            val newEnd = mapOriginalToTransformed(style.endOffset, markers)
            if (newStart < newEnd) { // Ensure valid range after transformation
                when (style.annotation) {
                    is SpanStyle -> AnnotatedString.Range(style.annotation, newStart, newEnd)
                    is ParagraphStyle -> AnnotatedString.Range(style.annotation, newStart, newEnd)
                    else -> style // Should not happen with current logic
                }
            } else {
                null // Marker consumed the whole range or invalid
            }
        }

        return TransformedText(
            text = AnnotatedString(
                text = transformedString,
                spanStyles = text.spanStyles + transformedStyles.filterIsInstance<AnnotatedString.Range<SpanStyle>>(),
                paragraphStyles = text.paragraphStyles + transformedStyles.filterIsInstance<AnnotatedString.Range<ParagraphStyle>>(),
            ),
            offsetMapping = MarkdownOffsetMapping(markers)
        )
    }

    // Helper function to map an original offset to a transformed one
    private fun mapOriginalToTransformed(originalOffset: Int, markers: List<NodeMarker>): Int {
        var removedLength = 0
        for (marker in markers) {
            if (originalOffset <= marker.startOffset) {
                break // Marker is after the offset
            }
            // If offset is within the marker, map it to the beginning of the marker's content (or end if no content)
            if (originalOffset < marker.endOffset) {
                // This part can be tricky. For simple markers, it maps to the start of the marker's content.
                // If a marker itself has content that is *not* removed, this logic needs to be more nuanced.
                // For now, assuming markers are fully removed:
                return marker.startOffset - removedLength
            }
            removedLength += marker.endOffset - marker.startOffset
        }
        return originalOffset - removedLength
    }


    private class MarkdownOffsetMapping(private val markers: List<NodeMarker>) : OffsetMapping {
        // Calculate cumulative removed lengths for efficiency
        private val cumulativeRemovedLengths = IntArray(markers.size + 1)
        private val originalMarkerStarts = IntArray(markers.size)
        private val transformedMarkerStarts = IntArray(markers.size)

        init {
            var currentRemovedLength = 0
            markers.forEachIndexed { index, marker ->
                cumulativeRemovedLengths[index] = currentRemovedLength
                originalMarkerStarts[index] = marker.startOffset
                transformedMarkerStarts[index] = marker.startOffset - currentRemovedLength

                currentRemovedLength += (marker.endOffset - marker.startOffset)
            }
            cumulativeRemovedLengths[markers.size] = currentRemovedLength
        }

        override fun originalToTransformed(offset: Int): Int {
            var removedLengthBeforeOrAtOffset = 0
            for (marker in markers) {
                if (offset <= marker.startOffset) {
                    // Offset is before or at the start of this marker
                    break
                }
                if (offset < marker.endOffset) {
                    // Offset is *inside* a marker. Map to the point where the marker's content begins in transformed text.
                    // This assumes the marker's content is `marker.transformedLength` long.
                    // `marker.startOffset - removedLengthBeforeOrAtOffset` gives the transformed start of the marker.
                    return marker.startOffset - removedLengthBeforeOrAtOffset

                }
                removedLengthBeforeOrAtOffset += (marker.endOffset - marker.startOffset)
            }
            return offset - removedLengthBeforeOrAtOffset
        }

        override fun transformedToOriginal(offset: Int): Int {
            var addedLength = 0
            for (marker in markers) {
                val markerOriginalLength = marker.endOffset - marker.startOffset
                val markerTransformedStartInOriginalContext = marker.startOffset - addedLength

                if (offset <= markerTransformedStartInOriginalContext) {
                    // Offset is before or within this marker's (potentially empty) transformed content.
                    // We need to map it back to the original string.
                    // If it's before the marker's transformed content, it's straightforward.
                    // If it's *within* the marker's transformed content, map it relative to marker.startOffset
                    if (offset <= markerTransformedStartInOriginalContext) {
                        return offset + addedLength
                    } else {
                        // Offset is within the transformed content of this marker
                        return marker.startOffset + (offset - markerTransformedStartInOriginalContext)
                    }
                }
                addedLength += markerOriginalLength
            }
            // If offset is after the last marker
            return offset + addedLength
        }
    }
}


