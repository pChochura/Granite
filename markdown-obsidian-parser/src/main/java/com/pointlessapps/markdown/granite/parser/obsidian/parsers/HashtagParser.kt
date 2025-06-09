package com.pointlessapps.markdown.granite.parser.obsidian.parsers

import com.pointlessapps.markdown.granite.parser.obsidian.ObsidianElementTypes
import com.pointlessapps.markdown.granite.parser.obsidian.ObsidianTokenTypes
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
            if (iterator.type == ObsidianTokenTypes.HASH) {
                val hashtag = parseHashtag(iterator)
                if (hashtag != null) {
                    iterator = hashtag.iteratorPosition.advance()
                    result = result.withOtherParsingResult(hashtag)

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
            val it = iterator.advance()
            if (it.type != ObsidianTokenTypes.HASHTAG_TEXT) return null

            return LocalParsingResult(
                iteratorPosition = it,
                parsedNodes = listOf(
                    SequentialParser.Node(
                        range = startIndex..it.index + 1,
                        type = ObsidianElementTypes.HASHTAG,
                    ),
                ),
                rangesToProcessFurther = emptyList(),
            )
        }
    }
}
