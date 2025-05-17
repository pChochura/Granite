package com.pointlessapps.obsidian_mini.markdown_renderer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFlatMap
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

// Data class to hold information about markers to be removed
data class MarkerInfo(val originalStart: Int, val originalEnd: Int, val transformedLength: Int = 0)

class MarkdownTransformation(
    private val activeBlockOriginalRange: TextRange?,
) : VisualTransformation {

    private val parser = MarkdownParser(GFMFlavourDescriptor())

    private val boldStyle = SpanStyle(fontWeight = FontWeight.Bold)
    private val italicStyle = SpanStyle(fontStyle = FontStyle.Italic)
    private val codeStyle = SpanStyle(
        background = Color.DarkGray.copy(alpha = 0.3f),
        fontFamily = FontFamily.Monospace,
    )
    private val headingStyles = listOf(
        SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        ) to ParagraphStyle(lineHeight = 2.em),
        SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        ) to ParagraphStyle(lineHeight = 1.74.em),
        SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        ) to ParagraphStyle(lineHeight = 1.52.em),
        SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp
        ) to ParagraphStyle(lineHeight = 1.32.em),
        SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        ) to ParagraphStyle(lineHeight = 1.15.em),
        SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        ) to ParagraphStyle(lineHeight = 1.em),
    )

    private fun IElementType.toHeadingLevel() = when (this) {
        MarkdownElementTypes.ATX_1 -> 0
        MarkdownElementTypes.ATX_2 -> 1
        MarkdownElementTypes.ATX_3 -> 2
        MarkdownElementTypes.ATX_4 -> 3
        MarkdownElementTypes.ATX_5 -> 4
        MarkdownElementTypes.ATX_6 -> 5
        else -> throw IllegalArgumentException("The provided IElementType is not a heading")
    }

    // Modified to return styles and marker info
    private fun List<ASTNode>.getStylesAndMarkers(
        wholeText: String,
    ): Pair<List<AnnotatedString.Range<AnnotatedString.Annotation>>, List<MarkerInfo>> {
        val styles = mutableListOf<AnnotatedString.Range<AnnotatedString.Annotation>>()
        val markers = mutableListOf<MarkerInfo>()

        fun processNode(node: ASTNode) {
            val isNodeActive = activeBlockOriginalRange?.intersects(
                TextRange(node.startOffset, node.endOffset),
            ) == false

            when (node.type) {
                MarkdownElementTypes.PARAGRAPH -> node.children.forEach(::processNode)

                MarkdownElementTypes.STRONG -> {
                    // Example: "**bold**" -> "bold"
                    // Markers: "**" at the start and "**" at the end
                    // Assuming CommonMark:
                    val openingMarker = node.children.takeWhile { it.type == MarkdownTokenTypes.EMPH }
                    val closingMarker = node.children.takeLastWhile { it.type == MarkdownTokenTypes.EMPH }

                    if (openingMarker.isNotEmpty() && closingMarker != null) {
                        if (isNodeActive) {
                            markers.add(MarkerInfo(openingMarker.minOf { it.startOffset }, openingMarker.maxOf { it.endOffset }))
                            markers.add(MarkerInfo(closingMarker.minOf { it.startOffset }, closingMarker.maxOf { it.endOffset }))
                        }
                        styles.add(
                            AnnotatedString.Range(
                                boldStyle,
                                openingMarker.minOf { it.startOffset }, // Apply style *after* opening marker
                                closingMarker.maxOf { it.endOffset }  // Apply style *before* closing marker
                            )
                        )
                    }
                }

                MarkdownElementTypes.EMPH -> {
                    // Example: "*italic*" -> "italic"
                    val openingMarker = node.children.find { it.type == MarkdownTokenTypes.EMPH }
                    val closingMarker = node.children.findLast { it.type == MarkdownTokenTypes.EMPH }

                    if (openingMarker != null && closingMarker != null && openingMarker != closingMarker) {
                        if (isNodeActive) {
                            markers.add(MarkerInfo(openingMarker.startOffset, openingMarker.endOffset))
                            markers.add(MarkerInfo(closingMarker.startOffset, closingMarker.endOffset))
                        }
                        styles.add(
                            AnnotatedString.Range(
                                italicStyle,
                                openingMarker.startOffset,
                                closingMarker.endOffset
                            )
                        )
                    }
                }

                MarkdownElementTypes.CODE_SPAN -> {
                    // Example: "`code`" -> "code"
                    val openingMarker = node.children.find { it.type == MarkdownTokenTypes.BACKTICK }
                    val closingMarker = node.children.findLast { it.type == MarkdownTokenTypes.BACKTICK }
                    if (openingMarker != null && closingMarker != null) {
                        if (isNodeActive) {
                            markers.add(MarkerInfo(openingMarker.startOffset, openingMarker.endOffset))
                            markers.add(MarkerInfo(closingMarker.startOffset, closingMarker.endOffset))
                        }
                        styles.add(
                            AnnotatedString.Range(
                                codeStyle,
                                openingMarker.startOffset,
                                closingMarker.endOffset
                            )
                        )
                    }
                }


                MarkdownElementTypes.ATX_1,
                MarkdownElementTypes.ATX_2,
                MarkdownElementTypes.ATX_3,
                MarkdownElementTypes.ATX_4,
                MarkdownElementTypes.ATX_5,
                MarkdownElementTypes.ATX_6,
                    -> {
                    val atxHeaderNode =
                        node.children.firstOrNull { it.type == MarkdownTokenTypes.ATX_HEADER } // The '#' characters

                    if (atxHeaderNode != null) {
                        var markerEndOffsetInParent = atxHeaderNode.endOffset
                        var contentStartOffsetInParent = atxHeaderNode.endOffset

                        // Check for a space immediately following the ATX_HEADER node within the parent (heading) node
                        // The text of the heading node starts at node.startOffset
                        val headingNodeText = wholeText.substring(node.startOffset, node.endOffset)

                        // Calculate the potential start of the actual content relative to the heading node's start
                        val potentialContentStartInHeadingNode =
                            atxHeaderNode.endOffset - node.startOffset

                        if (potentialContentStartInHeadingNode < headingNodeText.length &&
                            headingNodeText[potentialContentStartInHeadingNode] == ' '
                        ) {
                            // Space found after the marker, extend the marker to include this space
                            markerEndOffsetInParent = atxHeaderNode.endOffset + 1
                            contentStartOffsetInParent = atxHeaderNode.endOffset + 1
                        }
                        // else: no space, or something else immediately follows.
                        // contentStartOffsetInParent remains atxHeaderNode.endOffset

                        // Add the marker (e.g., "## " or "##")
                        if (isNodeActive) {
                            markers.add(MarkerInfo(node.startOffset, markerEndOffsetInParent))
                        }

                        val (spanStyle, paragraphStyle) = headingStyles[node.type.toHeadingLevel()]
                        styles.add(
                            AnnotatedString.Range(
                                spanStyle,
                                node.startOffset, // Style the text part after the marker (and potential space)
                                node.endOffset
                            )
                        )
                        styles.add(
                            AnnotatedString.Range(
                                paragraphStyle,
                                node.startOffset, // Apply paragraph style to the same range
                                node.endOffset
                            )
                        )
                    }
                    // Process children if headings can contain other styled elements (e.g. ## *Bold Heading*)
                    // However, usually the text part of a heading is plain.
                    node.children.filterNot { it.type == MarkdownTokenTypes.ATX_HEADER }
                        .forEach(::processNode)
                }
                MarkdownElementTypes.INLINE_LINK -> {
                    // Example: "[link text](url "title")" -> "link text" (styled)
                    // AST Structure for INLINE_LINK:
                    // - LINK_TEXT (contains the actual text)
                    //   - LBRACKET ([)
                    //   - TEXT (the link text itself)
                    //   - RBRACKET (])
                    // - LINK_DESTINATION (contains the URL)
                    // - LINK_TITLE (optional, contains the title)
                    // We also need to account for the '(' and ')' around the URL and title part.

                    val linkTextNode = node.children.find { it.type == MarkdownElementTypes.LINK_TEXT }
                    val linkDestinationNode = node.children.find { it.type == MarkdownElementTypes.LINK_DESTINATION }
                    // Optional: val linkTitleNode = node.children.find { it.type == MarkdownElementTypes.LINK_TITLE }

                    if (linkTextNode != null && linkDestinationNode != null) {
                        // 1. Identify markers and content range for the link text part
                        val lbracketNode = linkTextNode.children.find { it.type == MarkdownTokenTypes.LBRACKET }
                        val rbracketNode = linkTextNode.children.find { it.type == MarkdownTokenTypes.RBRACKET }

                        // The actual visible text is between LBRACK and RBRACK
                        // If LBRACK or RBRACK are missing from LINK_TEXT's children for some reason,
                        // we might need a fallback, but they should be there for valid INLINE_LINK.
                        if (lbracketNode != null && rbracketNode != null) {
                            val linkContentStart = lbracketNode.endOffset
                            val linkContentEnd = rbracketNode.startOffset

                            // Add marker for "["
                            if (isNodeActive) {
                                markers.add(MarkerInfo(lbracketNode.startOffset, lbracketNode.endOffset))
                            }

                            // Add styles for the link text content
                            val linkStyle = SpanStyle(color = Color.Blue, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline) // Example style
                            styles.add(
                                AnnotatedString.Range(
                                    linkStyle,
                                    node.startOffset,
                                    node.endOffset
                                )
                            )

                            // Add marker for "]"
                            if (isNodeActive) {
                                markers.add(MarkerInfo(rbracketNode.startOffset, rbracketNode.endOffset))
                            }

                            // 2. Identify marker for the URL and title part: "(url "title")"
                            // This whole part is removed. It starts after the ']' of LINK_TEXT
                            // and ends at the end of the INLINE_LINK node.
                            // The opening '(' of the URL part is typically not a direct child token of INLINE_LINK
                            // in the same way LBRACK is for LINK_TEXT. It's implied by the structure.
                            // The LINK_DESTINATION starts *after* the implicit '('.

                            // The start of the URL part is immediately after the RBRACKET of linkTextNode
                            val urlPartStartOffset = rbracketNode.endOffset
                            // The end of the URL part is the end of the whole INLINE_LINK node
                            val urlPartEndOffset = node.endOffset

                            if (urlPartStartOffset < urlPartEndOffset && isNodeActive) {
                                markers.add(MarkerInfo(urlPartStartOffset, urlPartEndOffset))
                            }

                            // Recursively process children *within* the link text, if any (e.g., "[*italic link*](url)")
                            // These children are inside the LINK_TEXT node, between LBRACK and RBRACK.
                            linkTextNode.children.filter { child ->
                                child.type != MarkdownTokenTypes.LBRACKET &&
                                        child.type != MarkdownTokenTypes.RBRACKET &&
                                        child.startOffset >= linkContentStart && // Ensure it's within the actual content
                                        child.endOffset <= linkContentEnd
                            }.forEach(::processNode)

                        } else {
                            // Fallback: if LBRACK or RBRACK not found, process children of linkTextNode directly
                            // This might not correctly remove the brackets if they are separate nodes.
                            linkTextNode.children.forEach(::processNode)
                        }
                    } else {
                        // Not a valid INLINE_LINK structure as expected, process all children
                        node.children.forEach(::processNode)
                    }
                }

                // Add other Markdown element types and their marker logic here
                // MarkdownElementTypes.CODE_BLOCK - needs careful handling of fence markers

                else -> {
                    // For elements that don't have explicit markers to remove but might contain styled children
                    node.children.forEach(::processNode)
                }
            }
        }

        this.forEach(::processNode)
        return styles to markers.sortedBy { it.originalStart } // Important to sort for OffsetMapping
    }

    override fun filter(text: AnnotatedString): TransformedText {
        val astRoot = parser.buildMarkdownTreeFromString(text.text)
        val (styles, markers) = astRoot.children.getStylesAndMarkers(text.text)

        // 1. Build the transformed string by removing markers
        val originalText = text.text
        val transformedTextBuilder = StringBuilder()
        var currentOriginalPos = 0
        for (marker in markers) {
            if (currentOriginalPos < marker.originalStart) {
                transformedTextBuilder.append(originalText.substring(currentOriginalPos, marker.originalStart))
            }
            // Content within the marker (if any, usually none for simple markers)
            // transformedTextBuilder.append(originalText.substring(marker.originalStart, marker.originalEnd).take(marker.transformedLength))
            currentOriginalPos = marker.originalEnd
        }
        if (currentOriginalPos < originalText.length) {
            transformedTextBuilder.append(originalText.substring(currentOriginalPos))
        }
        val transformedString = transformedTextBuilder.toString()

        // 2. Adjust style ranges for the transformed string
        val transformedStyles = styles.mapNotNull { range ->
            val newStart = mapOriginalToTransformed(range.start, markers)
            val newEnd = mapOriginalToTransformed(range.end, markers)
            if (newStart < newEnd) { // Ensure valid range after transformation
                when (range.item) {
                    is SpanStyle -> AnnotatedString.Range(range.item as SpanStyle, newStart, newEnd)
                    is ParagraphStyle -> AnnotatedString.Range(range.item as ParagraphStyle, newStart, newEnd)
                    else -> range // Should not happen with current logic
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
    private fun mapOriginalToTransformed(originalOffset: Int, markers: List<MarkerInfo>): Int {
        var removedLength = 0
        for (marker in markers) {
            if (originalOffset <= marker.originalStart) {
                break // Marker is after the offset
            }
            // If offset is within the marker, map it to the beginning of the marker's content (or end if no content)
            if (originalOffset < marker.originalEnd) {
                // This part can be tricky. For simple markers, it maps to the start of the marker's content.
                // If a marker itself has content that is *not* removed, this logic needs to be more nuanced.
                // For now, assuming markers are fully removed:
                return marker.originalStart - removedLength + marker.transformedLength
            }
            removedLength += (marker.originalEnd - marker.originalStart - marker.transformedLength)
        }
        return originalOffset - removedLength
    }


    private class MarkdownOffsetMapping(private val markers: List<MarkerInfo>) : OffsetMapping {
        // Calculate cumulative removed lengths for efficiency
        private val cumulativeRemovedLengths = IntArray(markers.size + 1)
        private val originalMarkerStarts = IntArray(markers.size)
        private val transformedMarkerStarts = IntArray(markers.size)

        init {
            var currentRemovedLength = 0
            markers.forEachIndexed { index, marker ->
                cumulativeRemovedLengths[index] = currentRemovedLength
                originalMarkerStarts[index] = marker.originalStart
                transformedMarkerStarts[index] = marker.originalStart - currentRemovedLength

                currentRemovedLength += (marker.originalEnd - marker.originalStart - marker.transformedLength)
            }
            cumulativeRemovedLengths[markers.size] = currentRemovedLength
        }

        override fun originalToTransformed(offset: Int): Int {
            var removedLengthBeforeOrAtOffset = 0
            for (marker in markers) {
                if (offset <= marker.originalStart) {
                    // Offset is before or at the start of this marker
                    break
                }
                if (offset < marker.originalEnd) {
                    // Offset is *inside* a marker. Map to the point where the marker's content begins in transformed text.
                    // This assumes the marker's content is `marker.transformedLength` long.
                    // `marker.originalStart - removedLengthBeforeOrAtOffset` gives the transformed start of the marker.
                    return (marker.originalStart - removedLengthBeforeOrAtOffset) + marker.transformedLength.coerceAtMost(offset - marker.originalStart)

                }
                removedLengthBeforeOrAtOffset += (marker.originalEnd - marker.originalStart - marker.transformedLength)
            }
            return offset - removedLengthBeforeOrAtOffset
        }

        override fun transformedToOriginal(offset: Int): Int {
            var addedLength = 0
            for (marker in markers) {
                val markerOriginalLength = marker.originalEnd - marker.originalStart
                val markerTransformedStartInOriginalContext = marker.originalStart - addedLength

                if (offset <= markerTransformedStartInOriginalContext + marker.transformedLength) {
                    // Offset is before or within this marker's (potentially empty) transformed content.
                    // We need to map it back to the original string.
                    // If it's before the marker's transformed content, it's straightforward.
                    // If it's *within* the marker's transformed content, map it relative to marker.originalStart
                    if (offset <= markerTransformedStartInOriginalContext) {
                        return offset + addedLength
                    } else {
                        // Offset is within the transformed content of this marker
                        return marker.originalStart + (offset - markerTransformedStartInOriginalContext)
                    }
                }
                addedLength += (markerOriginalLength - marker.transformedLength)
            }
            // If offset is after the last marker
            return offset + addedLength
        }
    }
}


