package com.pointlessapps.markdown.obsidian.parser.obsidian.parsers

import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianElementTypes
import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianTokenTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.parser.sequentialparsers.LocalParsingResult
import org.intellij.markdown.parser.sequentialparsers.RangesListBuilder
import org.intellij.markdown.parser.sequentialparsers.SequentialParser
import org.intellij.markdown.parser.sequentialparsers.TokensCache

/**
 * Parses a structure called [ObsidianElementTypes.HASHTAG] that is represented by
 * a hash symbol followed by a text: `#tag`.
 *
 * It can be inserted at any position in the markdown.
 */
internal class HashtagParser : SequentialParser {
    override fun parse(
        tokens: TokensCache,
        rangesToGlue: List<IntRange>,
    ): SequentialParser.ParsingResult {
        var result = SequentialParser.ParsingResultBuilder()
        val delegateIndices = RangesListBuilder()
        var iterator: TokensCache.Iterator = tokens.RangesListIterator(rangesToGlue)

        while (iterator.type != null) {
            if (iterator.type == ObsidianTokenTypes.HASH && !iterator.charLookup(1)
                    .isWhitespace()
            ) {
                val footnoteLink = parseHashtag(iterator)
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
        fun parseHashtag(iterator: TokensCache.Iterator): LocalParsingResult? {
            val startIndex = iterator.index
            // It was already checked that the iteration started with #
            var it = iterator.advance()

            if (it.type != MarkdownTokenTypes.TEXT && it.type != MarkdownTokenTypes.EMPH) {
                return null
            }

            while (it.type == MarkdownTokenTypes.TEXT || it.type == MarkdownTokenTypes.EMPH) {
                it = it.advance()
            }

            it = it.advance()

            return LocalParsingResult(
                iteratorPosition = it,
                parsedNodes = listOf(
                    SequentialParser.Node(
                        range = startIndex..it.index,
                        type = ObsidianElementTypes.HASHTAG,
                    ),
                ),
                rangesToProcessFurther = emptyList(),
            )
        }
    }
}
