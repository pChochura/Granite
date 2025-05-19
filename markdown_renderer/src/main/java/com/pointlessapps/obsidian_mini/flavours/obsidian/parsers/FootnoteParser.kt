package com.pointlessapps.obsidian_mini.flavours.obsidian.parsers

import com.pointlessapps.obsidian_mini.flavours.obsidian.ObsidianElementTypes
import com.pointlessapps.obsidian_mini.flavours.obsidian.ObsidianTokenTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.parser.sequentialparsers.LocalParsingResult
import org.intellij.markdown.parser.sequentialparsers.RangesListBuilder
import org.intellij.markdown.parser.sequentialparsers.SequentialParser
import org.intellij.markdown.parser.sequentialparsers.TokensCache

/**
 * Parses a structure called [ObsidianElementTypes.FOOTNOTE_LINK] that is represented by
 * an id ([ObsidianElementTypes.FOOTNOTE_ID]) encapsulated in a `[^ID]` block.
 * It can be inserted at any position in the markdown.
 */
internal class FootnoteParser : SequentialParser {
    override fun parse(
        tokens: TokensCache,
        rangesToGlue: List<IntRange>,
    ): SequentialParser.ParsingResult {
        var result = SequentialParser.ParsingResultBuilder()
        val delegateIndices = RangesListBuilder()
        var iterator: TokensCache.Iterator = tokens.RangesListIterator(rangesToGlue)

        while (iterator.type != null) {
            if (iterator.type == MarkdownTokenTypes.LBRACKET && iterator.rawLookup(1) == ObsidianTokenTypes.CARET) {
                val footnoteLink = parseFootnoteLink(iterator)
                if (footnoteLink != null) {
                    iterator = footnoteLink.iteratorPosition.advance()
                    result = result.withOtherParsingResult(footnoteLink)
                    continue
                }
            }

            delegateIndices.put(iterator.index)
            iterator = iterator.advance()
        }

        return result.withFurtherProcessing(delegateIndices.get())
    }

    private companion object {
        fun parseFootnoteLink(iterator: TokensCache.Iterator): LocalParsingResult? {
            val startIndex = iterator.index
            // It was already checked that the iteration started with [^
            var it = iterator.advance().advance()

            val delegate = RangesListBuilder()

            val idStartIndex = it.index
            while (it.type != MarkdownTokenTypes.RBRACKET && it.type != null) {
                delegate.put(it.index)
                it = it.advance()
            }
            val idEndIndex = it.index

            if (it.type != MarkdownTokenTypes.RBRACKET) {
                return null
            }
            it = it.advance()

            return LocalParsingResult(
                iteratorPosition = it,
                parsedNodes = listOf(
                    SequentialParser.Node(
                        range = startIndex..it.index,
                        type = ObsidianElementTypes.FOOTNOTE_LINK,
                    ),
                    SequentialParser.Node(
                        range = idStartIndex..idEndIndex,
                        type = ObsidianElementTypes.FOOTNOTE_ID,
                    ),
                ),
                rangesToProcessFurther = emptyList(),
            )
        }
    }
}
