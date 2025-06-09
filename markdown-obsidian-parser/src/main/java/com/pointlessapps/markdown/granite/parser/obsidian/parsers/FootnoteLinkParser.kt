package com.pointlessapps.markdown.granite.parser.obsidian.parsers

import com.pointlessapps.markdown.granite.parser.obsidian.ObsidianElementTypes
import com.pointlessapps.markdown.granite.parser.obsidian.ObsidianTokenTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.parser.sequentialparsers.LocalParsingResult
import org.intellij.markdown.parser.sequentialparsers.RangesListBuilder
import org.intellij.markdown.parser.sequentialparsers.SequentialParser
import org.intellij.markdown.parser.sequentialparsers.TokensCache

/**
 * Parses a structure called [ObsidianElementTypes.FOOTNOTE_LINK] that is represented by
 * an id ([ObsidianElementTypes.FOOTNOTE_ID]) encapsulated in a `[^ID]` block.
 *
 * It can be inserted at any position in the markdown.
 */
internal class FootnoteLinkParser : SequentialParser {
    override fun parse(
        tokens: TokensCache,
        rangesToGlue: List<IntRange>,
    ): SequentialParser.ParsingResult {
        var result = SequentialParser.ParsingResultBuilder()
        val delegateIndices = RangesListBuilder()
        var iterator: TokensCache.Iterator = tokens.RangesListIterator(rangesToGlue)

        while (iterator.type != null) {
            if (iterator.type == MarkdownTokenTypes.LBRACKET && iterator.rawLookup(1) == ObsidianTokenTypes.CARET) {
                val footnoteLink = parseFootnoteLink(iterator, tokens.originalText)
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
        fun parseFootnoteLink(
            iterator: TokensCache.Iterator,
            textContent: CharSequence,
        ): LocalParsingResult? {
            val startIndex = iterator.index
            // It was already checked that the iteration started with [^
            var it = iterator.advance().advance()

            val delegate = RangesListBuilder()

            val idStartTextIndex = it.start
            val idStartIndex = it.index
            while (it.type != MarkdownTokenTypes.RBRACKET && it.type != null) {
                delegate.put(it.index)
                it = it.advance()
            }
            val idEndIndex = it.index

            if (textContent.substring(idStartTextIndex, it.start).contains(REGEX)) {
                return null
            }

            if (it.type != MarkdownTokenTypes.RBRACKET) {
                return null
            }

            return LocalParsingResult(
                iteratorPosition = it,
                parsedNodes = listOf(
                    SequentialParser.Node(
                        range = startIndex..it.index + 1,
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

        val REGEX = Regex(" |(?!<\\\\)]")
    }
}
