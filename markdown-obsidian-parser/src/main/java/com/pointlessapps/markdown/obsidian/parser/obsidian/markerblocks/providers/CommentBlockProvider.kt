package com.pointlessapps.markdown.obsidian.parser.obsidian.markerblocks.providers

import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianElementTypes
import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianTokenTypes
import com.pointlessapps.markdown.obsidian.parser.obsidian.markerblocks.CommentMarkerBlock
import org.intellij.markdown.parser.LookaheadText
import org.intellij.markdown.parser.MarkerProcessor
import org.intellij.markdown.parser.ProductionHolder
import org.intellij.markdown.parser.constraints.MarkdownConstraints
import org.intellij.markdown.parser.markerblocks.MarkerBlock
import org.intellij.markdown.parser.markerblocks.MarkerBlockProvider
import org.intellij.markdown.parser.sequentialparsers.SequentialParser

/**
 * A provider for the [CommentMarkerBlock] that encapsulates the multiline comments.
 * A [ObsidianElementTypes.COMMENT_BLOCK] consists of a text wrapped by double [ObsidianTokenTypes.PERCENT]
 *
 * It can be inserted only at the start of a new line and spans across until its end.
 */
internal class CommentBlockProvider : MarkerBlockProvider<MarkerProcessor.StateInfo> {

    override fun createMarkerBlocks(
        pos: LookaheadText.Position,
        productionHolder: ProductionHolder,
        stateInfo: MarkerProcessor.StateInfo,
    ): List<MarkerBlock> {
        // Ensure we start at the beginning of the line
        if (!MarkerBlockProvider.isStartOfLineWithConstraints(pos, stateInfo.currentConstraints)) {
            return emptyList()
        }

        val matchResult = matchCommentBlockDefinition(
            text = pos.originalText,
            startOffset = pos.offset,
        ) ?: return emptyList()

        productionHolder.addProduction(
            matchResult.mapIndexed { index, range ->
                SequentialParser.Node(
                    range = range,
                    type = when (index) {
                        0 -> ObsidianTokenTypes.PERCENT
                        1 -> ObsidianElementTypes.COMMENT_BLOCK_CONTENT
                        2 -> ObsidianTokenTypes.PERCENT
                        else -> throw IllegalStateException("Unexpected group in footnote regex")
                    }
                )
            },
        )

        return listOf(
            CommentMarkerBlock(
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
        fun matchCommentBlockDefinition(text: CharSequence, startOffset: Int): List<IntRange>? {
            var offset = MarkerBlockProvider.passSmallIndent(text, startOffset)

            if (offset + 2 > text.length) return null
            val openingMatch = IntRange(offset, offset + 2)
                .takeIf { text.substring(it.first, it.last) == "%%" } ?: return null
            offset += 2

            val contentMatch = matchCommentContent(text, offset) ?: return null
            offset = contentMatch.last

            if (offset + 2 > text.length) return null
            val closingMatch = IntRange(offset, offset + 2)
                .takeIf { text.substring(it.first, it.last) == "%%" } ?: return null

            return listOf(openingMatch, contentMatch, closingMatch)
        }

        fun matchCommentContent(text: CharSequence, start: Int): IntRange? {
            if (start >= text.length) return null

            var offset = start
            while (offset < text.length) {
                if (text[offset] == '%' && text.getOrNull(offset + 1) == '%') {
                    break
                }

                offset++
            }

            // There's no content
            if (start >= offset - 1) {
                return null
            }

            return IntRange(start, offset)
        }

        val REGEX = Regex("^ {0,3}(%%+).*\$")
    }
}
