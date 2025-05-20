package com.pointlessapps.markdown.obsidian.parser.obsidian.parsers

import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.parser.sequentialparsers.LocalParsingResult
import org.intellij.markdown.parser.sequentialparsers.RangesListBuilder
import org.intellij.markdown.parser.sequentialparsers.SequentialParser
import org.intellij.markdown.parser.sequentialparsers.TokensCache

/**
 * Parses a structure called [ObsidianElementTypes.INTERNAL_LINK] that is represented by a text
 * ([MarkdownElementTypes.LINK_DESTINATION]) surrounded by double brackets: `[[DESTINATION]]`.
 * It can be inserted at any position in the markdown.
 */
internal class InternalLinkParser : SequentialParser {

    override fun parse(
        tokens: TokensCache,
        rangesToGlue: List<IntRange>,
    ): SequentialParser.ParsingResult {
        var result = SequentialParser.ParsingResultBuilder()
        val delegateIndices = RangesListBuilder()
        var iterator: TokensCache.Iterator = tokens.RangesListIterator(rangesToGlue)

        while (iterator.type != null) {
            if (iterator.type == MarkdownTokenTypes.LBRACKET) {
                val inlineLink = parseInternalLink(iterator)
                if (inlineLink != null) {
                    iterator = inlineLink.iteratorPosition.advance()
                    result = result.withOtherParsingResult(inlineLink)
                    continue
                }
            }

            delegateIndices.put(iterator.index)
            iterator = iterator.advance()
        }

        return result.withFurtherProcessing(delegateIndices.get())
    }

    private companion object {
        fun parseInternalLink(iterator: TokensCache.Iterator): LocalParsingResult? {
            val startIndex = iterator.index
            var it = iterator

            if (it.rawLookup(1) != MarkdownTokenTypes.LBRACKET) {
                return null
            }

            it = it.advance().advance()
            val linkDestination = parseInternalLinkDestination(it) ?: return null
            it = linkDestination.iteratorPosition

            // The closing of the internal link (]]) is not present
            if (it.type != MarkdownTokenTypes.RBRACKET || it.rawLookup(1) != MarkdownTokenTypes.RBRACKET) {
                return null
            }

            return LocalParsingResult(
                iteratorPosition = it,
                parsedNodes = linkDestination.parsedNodes + SequentialParser.Node(
                    startIndex..it.index + 2,
                    ObsidianElementTypes.INTERNAL_LINK,
                ),
                rangesToProcessFurther = linkDestination.rangesToProcessFurther,
            )
        }

        fun parseInternalLinkDestination(iterator: TokensCache.Iterator): LocalParsingResult? {
            val startIndex = iterator.index
            var it = iterator

            val delegate = RangesListBuilder()

            while (it.type != MarkdownTokenTypes.RBRACKET && it.type != null) {
                delegate.put(it.index)
                if (it.type == MarkdownTokenTypes.LBRACKET) {
                    break
                }

                it = it.advance()
            }

            if (it.type == MarkdownTokenTypes.RBRACKET) {
                val endIndex = it.index

                // The link is empty
                if (endIndex == startIndex) {
                    return null
                }

                return LocalParsingResult(
                    iteratorPosition = it,
                    parsedNodes = listOf(
                        SequentialParser.Node(startIndex..endIndex, MarkdownElementTypes.LINK_DESTINATION),
                    ),
                    delegateRanges = delegate.get(),
                )
            }

            return null
        }
    }
}
