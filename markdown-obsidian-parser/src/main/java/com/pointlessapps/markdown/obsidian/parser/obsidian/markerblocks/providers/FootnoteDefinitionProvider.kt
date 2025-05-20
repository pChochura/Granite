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
            }
        )

        return listOf(
            FootnoteDefinitionMarkerBlock(
                myConstraints = stateInfo.currentConstraints,
                marker = productionHolder.mark(),
                endPosition = matchResult.last().last + 1,
            ),
        )
    }

    override fun interruptsParagraph(
        pos: LookaheadText.Position,
        constraints: MarkdownConstraints,
    ) = false

    private companion object {
        fun matchFootnoteDefinition(text: CharSequence, startOffset: Int): List<IntRange>? {
            var offset = MarkerBlockProvider.passSmallIndent(text, startOffset)

            if (offset + 1 >= text.length) return null
            val lBracketMatch = IntRange(offset, offset + 1)
                .takeIf {text[offset] == '[' } ?: return null
            offset++

            if (offset + 1 >= text.length) return null
            val caretMatch = IntRange(offset, offset + 1)
                .takeIf { text[offset] == '^' } ?: return null
            offset++

            val labelMatch = matchFootnoteLabel(text, offset) ?: return null
            offset = labelMatch.last

            if (offset + 1 >= text.length) return null
            val rBracketMatch = IntRange(offset, offset + 1)
                .takeIf { text[offset] == ']' } ?: return null
            offset++

            if (offset + 1 >= text.length) return null
            val colonMatch = IntRange(offset, offset + 1)
                .takeIf { text[offset] == ':' } ?: return null
            offset++

            // Skip spaces before the content
            offset = passSpaces(text, offset)

            val contentMatch = matchFootnoteContent(text, offset) ?: return null

            return listOf(lBracketMatch, caretMatch, labelMatch, rBracketMatch, colonMatch, contentMatch)
        }

        private fun matchFootnoteLabel(text: CharSequence, start: Int): IntRange? {
            var offset = start
            while (offset < text.length && text[offset] != ']') {
                if (text[offset] in listOf('[', '\n')) return null

                offset++
            }

            return IntRange(start, offset).takeIf { offset != start }
        }

        private fun matchFootnoteContent(text: CharSequence, start: Int): IntRange? {
            if (start >= text.length) return null

            var offset = start
            while (offset < text.length && text[offset] != '\n') {
                offset++
            }

            // Ignore the newline character
            return IntRange(start, offset - 1)
        }

        private fun passSpaces(text: CharSequence, start: Int): Int {
            var offset = start
            while (offset < text.length && text[offset].isWhitespace()) {
                offset++
            }

            return offset
        }

    }
}
