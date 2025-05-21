package com.pointlessapps.markdown.obsidian.parser.obsidian.parsers

import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianElementTypes
import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianTokenTypes
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.parser.sequentialparsers.LocalParsingResult
import org.intellij.markdown.parser.sequentialparsers.RangesListBuilder
import org.intellij.markdown.parser.sequentialparsers.SequentialParser
import org.intellij.markdown.parser.sequentialparsers.TokensCache

/**
 * Parses a structure called [ObsidianElementTypes.EMBED] that is represented by a text
 * ([MarkdownElementTypes.LINK_DESTINATION]) in a format of: `![[DESTINATION]]`.
 *
 * Optionally it can contain a [MarkdownElementTypes.LINK_LABEL] in a form of `![[DESTINATION|LABEL]]`.
 *
 * It can be inserted at any position in the markdown.
 */
internal class EmbedParser : SequentialParser {

    override fun parse(
        tokens: TokensCache,
        rangesToGlue: List<IntRange>,
    ): SequentialParser.ParsingResult {
        var result = SequentialParser.ParsingResultBuilder()
        val delegateIndices = RangesListBuilder()
        var iterator: TokensCache.Iterator = tokens.RangesListIterator(rangesToGlue)

        while (iterator.type != null) {
            if (iterator.type == MarkdownTokenTypes.EXCLAMATION_MARK) {
                val inlineLink = parseEmbed(iterator)
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
        fun parseEmbed(iterator: TokensCache.Iterator): LocalParsingResult? {
            val startIndex = iterator.index
            var it = iterator

            if (
                it.rawLookup(1) != MarkdownTokenTypes.LBRACKET &&
                it.rawLookup(2) != MarkdownTokenTypes.LBRACKET
            ) {
                return null
            }

            it = it.advance().advance().advance()
            val linkDestination = parseInternalLinkDestination(it) ?: return null
            it = linkDestination.iteratorPosition

            val linkLabel = parseInternalLinkLabel(it)
            it = linkLabel?.iteratorPosition ?: it

            // The closing of the internal link (]]) is not present
            if (it.type != MarkdownTokenTypes.RBRACKET || it.rawLookup(1) != MarkdownTokenTypes.RBRACKET) {
                return null
            }

            return LocalParsingResult(
                iteratorPosition = it,
                parsedNodes = linkDestination.parsedNodes +
                        linkLabel?.parsedNodes.orEmpty() +
                        SequentialParser.Node(
                            range = startIndex..it.index + 2,
                            type = ObsidianElementTypes.EMBED,
                        ),
                rangesToProcessFurther = linkDestination.rangesToProcessFurther,
            )
        }

        fun parseInternalLinkDestination(iterator: TokensCache.Iterator): LocalParsingResult? {
            val startIndex = iterator.index
            var it = iterator

            val delegate = RangesListBuilder()

            while (it.type != MarkdownTokenTypes.RBRACKET && it.type != ObsidianTokenTypes.PIPE && it.type != null) {
                delegate.put(it.index)
                if (it.type == MarkdownTokenTypes.LBRACKET) {
                    break
                }

                it = it.advance()
            }

            if (it.type == MarkdownTokenTypes.RBRACKET || it.type == ObsidianTokenTypes.PIPE) {
                val endIndex = it.index

                // The link is empty
                if (endIndex == startIndex) {
                    return null
                }

                return LocalParsingResult(
                    iteratorPosition = it,
                    parsedNodes = listOf(
                        SequentialParser.Node(
                            range = startIndex..endIndex,
                            type = MarkdownElementTypes.LINK_DESTINATION,
                        ),
                    ),
                    delegateRanges = delegate.get(),
                )
            }

            return null
        }

        fun parseInternalLinkLabel(iterator: TokensCache.Iterator): LocalParsingResult? {
            var it = iterator

            if (it.type != ObsidianTokenTypes.PIPE) {
                return null
            }
            it = it.advance()
            val startIndex = it.index
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

                // The label is empty
                if (endIndex == startIndex) {
                    return null
                }

                return LocalParsingResult(
                    iteratorPosition = it,
                    parsedNodes = listOf(
                        SequentialParser.Node(
                            range = startIndex..endIndex,
                            type = MarkdownElementTypes.LINK_LABEL,
                        ),
                    ),
                    delegateRanges = delegate.get(),
                )
            }

            return null
        }
    }
}
