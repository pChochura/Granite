package com.pointlessapps.markdown.obsidian.parser.obsidian.markerblocks.providers

import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianElementTypes
import com.pointlessapps.markdown.obsidian.parser.obsidian.markerblocks.BlockQuoteMarkerBlock
import com.pointlessapps.markdown.obsidian.parser.obsidian.markerblocks.CommentMarkerBlock
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.parser.LookaheadText
import org.intellij.markdown.parser.MarkerProcessor
import org.intellij.markdown.parser.ProductionHolder
import org.intellij.markdown.parser.constraints.MarkdownConstraints
import org.intellij.markdown.parser.markerblocks.MarkerBlock
import org.intellij.markdown.parser.markerblocks.MarkerBlockProvider
import org.intellij.markdown.parser.sequentialparsers.SequentialParser

/**
 * A provider for the [CommentMarkerBlock] that encapsulates the block quotes - subsequent lines
 * starting with a `>` symbol.
 *
 * A [MarkdownElementTypes.BLOCK_QUOTE] consists of a sequence of [MarkdownTokenTypes.BLOCK_QUOTE]
 * followed by [ObsidianElementTypes.BLOCK_QUOTE_CONTENT].
 *
 * Additionally if the block quote's first line looks like: `> [!TYPE] TITLE`, the whole block
 * will be treated as [ObsidianElementTypes.CALLOUT], where:
 *
 * - `TYPE` - [ObsidianElementTypes.CALLOUT_TYPE]
 * - `TITLE` - [ObsidianElementTypes.CALLOUT_TITLE]
 *
 * It can be inserted only at the start of a new line and spans across until its end.
 */
internal class BlockQuoteProvider : MarkerBlockProvider<MarkerProcessor.StateInfo> {

    override fun createMarkerBlocks(
        pos: LookaheadText.Position,
        productionHolder: ProductionHolder,
        stateInfo: MarkerProcessor.StateInfo,
    ): List<MarkerBlock> {
        // Ensure we start at the beginning of the line
        if (!MarkerBlockProvider.isStartOfLineWithConstraints(pos, stateInfo.currentConstraints)) {
            return emptyList()
        }

        val matchResult = matchBlockQuoteDefinition(
            text = pos.originalText,
            startOffset = pos.offset,
        )

        if (matchResult.isEmpty()) return emptyList()

        productionHolder.addProduction(matchResult)

        return listOf(
            BlockQuoteMarkerBlock(
                myConstraints = stateInfo.currentConstraints,
                marker = productionHolder.mark(),
                endPosition = matchResult.maxOf { it.range.last },
                isCallout = matchResult.find { it.type == ObsidianElementTypes.CALLOUT_TYPE } != null,
            ),
        )
    }

    override fun interruptsParagraph(
        pos: LookaheadText.Position,
        constraints: MarkdownConstraints,
    ): Boolean = true

    private companion object {
        fun matchBlockQuoteDefinition(
            text: CharSequence,
            startOffset: Int,
        ): List<SequentialParser.Node> {
            var offset = startOffset
            var firstLine = true
            val matches = mutableListOf<SequentialParser.Node>()

            do {
                offset = passSpaces(text, offset)
                if (text.getOrNull(offset) == '\n') break
                if (text.getOrNull(offset) == '>') {
                    matches.add(
                        SequentialParser.Node(
                            range = offset..offset + 1,
                            type = MarkdownTokenTypes.BLOCK_QUOTE,
                        ),
                    )
                    offset++
                } else if (firstLine) break

                if (firstLine) {
                    val calloutTypeMatch = matchCalloutType(text, offset)
                    if (calloutTypeMatch != null) {
                        matches.add(
                            SequentialParser.Node(
                                range = calloutTypeMatch,
                                type = ObsidianElementTypes.CALLOUT_TYPE,
                            ),
                        )
                        offset = calloutTypeMatch.last + 1

                        val calloutTitleMatch = matchCalloutTitle(text, offset)
                        if (calloutTitleMatch != null) {
                            matches.add(
                                SequentialParser.Node(
                                    range = calloutTitleMatch,
                                    type = ObsidianElementTypes.CALLOUT_TITLE,
                                ),
                            )

                            offset = calloutTitleMatch.last + 1
                            firstLine = false
                            continue
                        }
                    }
                }

                val contentMatch = matchBlockQuoteContent(text, offset)
                if (contentMatch != null) {
                    matches.add(
                        SequentialParser.Node(
                            range = contentMatch,
                            type = ObsidianElementTypes.BLOCK_QUOTE_CONTENT,
                        ),
                    )
                } else {
                    break
                }

                offset = contentMatch.last + 1
                firstLine = false
            } while (offset < text.length)

            return matches
        }

        fun matchCalloutType(text: CharSequence, start: Int): IntRange? {
            if (start >= text.length) return null

            var offset = start
            if (text[offset] == ' ') offset++

            if (
                offset + 2 > text.length ||
                text.substring(offset, offset + 2) != "[!"
            ) return null
            offset += 2

            val startOffset = offset
            while (offset < text.length && text[offset] != ']') {
                offset++
            }

            // There's no content
            if (startOffset >= offset) {
                return null
            }

            return IntRange(startOffset, offset)
        }

        fun matchCalloutTitle(text: CharSequence, start: Int): IntRange? {
            if (start >= text.length) return null

            var offset = start
            if (text[offset] == ' ') offset++
            else return null

            while (offset < text.length && text[offset] != '\n') {
                offset++
            }

            // There's no content
            if (start >= offset) {
                return null
            }

            return IntRange(start, offset)
        }

        fun matchBlockQuoteContent(text: CharSequence, start: Int): IntRange? {
            if (start >= text.length) return null

            var offset = start
            while (offset < text.length && text[offset] != '\n') {
                offset++
            }

            // There's no content
            if (start >= offset) {
                return null
            }

            return IntRange(start, offset)
        }

        fun passSpaces(text: CharSequence, start: Int): Int {
            var offset = start
            while (offset < text.length && text[offset] == ' ') {
                offset++
            }

            return offset
        }
    }
}