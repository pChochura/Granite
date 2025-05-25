package com.pointlessapps.markdown.obsidian.parser.obsidian.markerblocks.providers

import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianElementTypes
import com.pointlessapps.markdown.obsidian.parser.obsidian.markerblocks.BlockQuoteMarkerBlock
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.parser.LookaheadText
import org.intellij.markdown.parser.MarkerProcessor
import org.intellij.markdown.parser.ProductionHolder
import org.intellij.markdown.parser.constraints.MarkdownConstraints
import org.intellij.markdown.parser.markerblocks.MarkerBlock
import org.intellij.markdown.parser.markerblocks.MarkerBlockProvider
import org.intellij.markdown.parser.sequentialparsers.SequentialParser

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
            val matches = mutableListOf<SequentialParser.Node>()

            do {
                offset = passSpaces(text, offset)
                if (text.getOrNull(offset) == '>') {
                    matches.add(
                        SequentialParser.Node(
                            range = offset..offset + 1,
                            type = MarkdownTokenTypes.BLOCK_QUOTE,
                        ),
                    )
                } else {
                    break
                }
                offset++

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
                offset += contentMatch.last - contentMatch.first + 1
            } while (offset < text.length)

            return matches
        }

        fun matchBlockQuoteContent(text: CharSequence, start: Int): IntRange? {
            if (start >= text.length) return null

            var offset = start
            while (offset < text.length) {
                if (text[offset] == '\n') {
                    val tempOffset = passSpaces(text, offset + 1)
                    if (text.getOrNull(tempOffset) in listOf('>', '\n', null)) break
                }

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