package com.pointlessapps.markdown.obsidian.parser.obsidian.markerblocks.providers

import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianElementTypes
import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianTokenTypes
import com.pointlessapps.markdown.obsidian.parser.obsidian.markerblocks.FootnoteDefinitionMarkerBlock
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.parser.LookaheadText
import org.intellij.markdown.parser.MarkerProcessor
import org.intellij.markdown.parser.ProductionHolder
import org.intellij.markdown.parser.constraints.MarkdownConstraints
import org.intellij.markdown.parser.markerblocks.MarkerBlock
import org.intellij.markdown.parser.markerblocks.MarkerBlockProvider
import org.intellij.markdown.parser.sequentialparsers.SequentialParser

/**
 * A provider for the [FootnoteDefinitionMarkerBlock] that encapsulates the footnote definitions.
 * A [ObsidianElementTypes.FOOTNOTE_DEFINITION] consists of `[^ID]: CONTENT`.
 *
 * - `ID` - [ObsidianElementTypes.FOOTNOTE_ID]
 * - `CONTENT` - [ObsidianElementTypes.FOOTNOTE_DEFINITION_TEXT]
 *
 * It can be inserted only at the start of a new line and spans across until its end.
 */
internal class FootnoteDefinitionProvider : MarkerBlockProvider<MarkerProcessor.StateInfo> {

    override fun createMarkerBlocks(
        pos: LookaheadText.Position,
        productionHolder: ProductionHolder,
        stateInfo: MarkerProcessor.StateInfo,
    ): List<MarkerBlock> {
        // Ensure we start at the beginning of the line
        if (!MarkerBlockProvider.isStartOfLineWithConstraints(pos, stateInfo.currentConstraints)) {
            return emptyList()
        }

        val matchResult = matchFootnoteDefinition(
            text = pos.originalText,
            startOffset = pos.offset,
        ) ?: return emptyList()

        productionHolder.addProduction(
            matchResult.mapIndexed { index, range ->
                SequentialParser.Node(
                    range = range,
                    type = when (index) {
                        0 -> MarkdownTokenTypes.LBRACKET
                        1 -> ObsidianTokenTypes.CARET
                        2 -> ObsidianElementTypes.FOOTNOTE_ID
                        3 -> MarkdownTokenTypes.RBRACKET
                        4 -> MarkdownTokenTypes.COLON
                        5 -> ObsidianElementTypes.FOOTNOTE_DEFINITION_TEXT
                        else -> throw IllegalStateException("Unexpected group in footnote regex")
                    }
                )
            },
        )

        return listOf(
            FootnoteDefinitionMarkerBlock(
                myConstraints = stateInfo.currentConstraints,
                marker = productionHolder.mark(),
                endPosition = matchResult.last().last,
            ),
        )
    }

    override fun interruptsParagraph(
        pos: LookaheadText.Position,
        constraints: MarkdownConstraints,
    ): Boolean {
        if (!MarkerBlockProvider.isStartOfLineWithConstraints(pos, constraints)) {
            return false
        }

        return REGEX.containsMatchIn(pos.currentLineFromPosition)
    }

    private companion object {
        fun matchFootnoteDefinition(text: CharSequence, startOffset: Int): List<IntRange>? {
            val markers = matchFootnoteMarker(text, startOffset)

            if (markers.isEmpty()) return null

            // Skip spaces before the content
            val offset = passSpacesAndNewLine(text, markers.maxOf { it.last })

            val contentMatch = matchFootnoteContent(text, offset) ?: return null

            return buildList {
                addAll(markers)
                add(contentMatch)
            }
        }

        fun matchFootnoteMarker(text: CharSequence, start: Int): List<IntRange> {
            var offset = MarkerBlockProvider.passSmallIndent(text, start)

            if (offset + 1 > text.length) return emptyList()
            val lBracketMatch = IntRange(offset, offset + 1)
                .takeIf { text[offset] == '[' } ?: return emptyList()
            offset++

            if (offset + 1 > text.length) return emptyList()
            val caretMatch = IntRange(offset, offset + 1)
                .takeIf { text[offset] == '^' } ?: return emptyList()
            offset++

            val labelMatch = matchFootnoteLabel(text, offset) ?: return emptyList()
            offset = labelMatch.last

            if (offset + 1 > text.length) return emptyList()
            val rBracketMatch = IntRange(offset, offset + 1)
                .takeIf { text[offset] == ']' } ?: return emptyList()
            offset++

            if (offset + 1 > text.length) return emptyList()
            val colonMatch = IntRange(offset, offset + 1)
                .takeIf { text[offset] == ':' } ?: return emptyList()
            offset++

            return listOf(lBracketMatch, caretMatch, labelMatch, rBracketMatch, colonMatch)
        }

        fun matchFootnoteLabel(text: CharSequence, start: Int): IntRange? {
            var offset = start
            while (offset < text.length && text[offset] != ']') {
                if (text[offset] in listOf('[', '\n')) return null

                offset++
            }

            return IntRange(start, offset).takeIf { offset != start }
        }

        fun matchFootnoteContent(text: CharSequence, start: Int): IntRange? {
            if (start >= text.length) return null

            var offset = start
            while (offset < text.length) {
                if (text[offset] == '\n') {
                    if (
                        text.getOrNull(offset + 1) in listOf('\n', null) ||
                        matchFootnoteMarker(text, offset + 1).isNotEmpty()
                    ) {
                        break
                    }
                }

                offset++
            }

            // There's no content
            if (start >= offset) {
                return null
            }

            return IntRange(start, offset)
        }

        fun passSpacesAndNewLine(text: CharSequence, start: Int): Int {
            var offset = start
            var newLinePassed = false
            while (offset < text.length && text[offset].isWhitespace()) {
                if (text[offset] == '\n') {
                    if (newLinePassed) {
                        // Allow only one newline
                        return offset
                    }

                    newLinePassed = true
                }
                offset++
            }

            return offset
        }

        val REGEX = Regex("^ {0,3}\\[.*]: ?.*\$")
    }
}
