package com.pointlessapps.markdown.obsidian.parser.obsidian.parsers

import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianElementTypes
import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianTokenTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.parser.sequentialparsers.LocalParsingResult
import org.intellij.markdown.parser.sequentialparsers.RangesListBuilder
import org.intellij.markdown.parser.sequentialparsers.SequentialParser
import org.intellij.markdown.parser.sequentialparsers.TokensCache

/**
 * Parses a structure called [ObsidianElementTypes.INLINE_FOOTNOTE] that is represented by
 * an id ([ObsidianElementTypes.FOOTNOTE_DEFINITION_TEXT]) encapsulated in a `^[CONTENT]` block.
 *
 * It can be inserted at any position in the markdown.
 */
internal class InlineFootnoteParser : SequentialParser {
    override fun parse(
        tokens: TokensCache,
        rangesToGlue: List<IntRange>,
    ): SequentialParser.ParsingResult {
        var result = SequentialParser.ParsingResultBuilder()
        val delegateIndices = RangesListBuilder()
        var iterator: TokensCache.Iterator = tokens.RangesListIterator(rangesToGlue)

        while (iterator.type != null) {
            if (iterator.type == ObsidianTokenTypes.CARET && iterator.rawLookup(1) == MarkdownTokenTypes.LBRACKET) {
                val inlineFootnoteResult = parseInlineFootnote(iterator)
                if (inlineFootnoteResult != null) {
                    iterator = inlineFootnoteResult.iteratorPosition.advance()
                    result = result.withOtherParsingResult(inlineFootnoteResult)
                    continue
                }
            }

            delegateIndices.put(iterator.index)
            iterator = iterator.advance()
        }

        return result.withFurtherProcessing(delegateIndices.get())
    }

    private companion object {
        fun parseInlineFootnote(iterator: TokensCache.Iterator): LocalParsingResult? {
            val startIndex = iterator.index
            // It was already checked that the iteration started with ^[
            var it = iterator.advance().advance()

            val delegate = RangesListBuilder()

            val contentStartIndex = it.index
            while (
                it.type != MarkdownTokenTypes.RBRACKET &&
                it.type != MarkdownTokenTypes.EOL &&
                it.type != null
            ) {
                delegate.put(it.index)
                it = it.advance()
            }
            val contentEndIndex = it.index

            if (it.type != MarkdownTokenTypes.RBRACKET) {
                return null
            }
            it = it.advance()

            return LocalParsingResult(
                iteratorPosition = it,
                parsedNodes = listOf(
                    SequentialParser.Node(
                        range = startIndex..it.index,
                        type = ObsidianElementTypes.INLINE_FOOTNOTE,
                    ),
                    SequentialParser.Node(
                        range = contentStartIndex..contentEndIndex,
                        type = ObsidianElementTypes.FOOTNOTE_DEFINITION_TEXT,
                    ),
                ),
                rangesToProcessFurther = listOf(listOf(contentStartIndex..contentEndIndex)),
            )
        }
    }
}
