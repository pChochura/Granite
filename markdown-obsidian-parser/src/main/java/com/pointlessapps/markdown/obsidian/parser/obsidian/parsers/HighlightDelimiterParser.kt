package com.pointlessapps.markdown.obsidian.parser.obsidian.parsers

import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianElementTypes
import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianTokenTypes
import org.intellij.markdown.parser.sequentialparsers.DelimiterParser
import org.intellij.markdown.parser.sequentialparsers.SequentialParser
import org.intellij.markdown.parser.sequentialparsers.TokensCache
import org.intellij.markdown.parser.sequentialparsers.impl.EmphStrongDelimiterParser

/**
 * Parses a structure called [ObsidianElementTypes.HIGHLIGHT] that is represented by a text
 * surrounded by two or more equals signs (=).
 * It can be inserted at any position in the markdown.
 */
internal class HighlightDelimiterParser : DelimiterParser() {
    override fun scan(
        tokens: TokensCache,
        iterator: TokensCache.Iterator,
        delimiters: MutableList<Info>,
    ): Int {
        if (iterator.type != ObsidianTokenTypes.EQ) {
            return 0
        }

        // Count how many of '=' this token will consist of
        var stepsToAdvance = 1
        var rightIterator = iterator
        for (index in 0 until maxAdvance) {
            if (rightIterator.rawLookup(1) != ObsidianTokenTypes.EQ) {
                break
            }
            rightIterator = rightIterator.advance()
            stepsToAdvance += 1
        }

        val (canOpen, canClose) = canOpenClose(tokens, iterator, rightIterator, canSplitText = true)
        for (index in 0 until stepsToAdvance) {
            val info = Info(
                tokenType = ObsidianTokenTypes.EQ,
                position = iterator.index + index,
                length = 0,
                canOpen = canOpen,
                canClose = canClose,
                marker = '=',
            )
            delimiters.add(info)
        }
        return stepsToAdvance
    }

    override fun process(
        tokens: TokensCache,
        iterator: TokensCache.Iterator,
        delimiters: MutableList<Info>,
        result: SequentialParser.ParsingResultBuilder,
    ) {
        var shouldSkipNext = false
        delimiters.indices.reversed().forEach { index ->
            if (shouldSkipNext) {
                shouldSkipNext = false
                return@forEach
            }
            val opener = delimiters[index]
            if (opener.tokenType != ObsidianTokenTypes.EQ || opener.closerIndex == -1) {
                return@forEach
            }
            shouldSkipNext = EmphStrongDelimiterParser.areAdjacentSameMarkers(
                delimiters = delimiters,
                openerIndex = index,
                closerIndex = opener.closerIndex,
            )
            val closer = delimiters[opener.closerIndex]
            if (shouldSkipNext) {
                val node = SequentialParser.Node(
                    range = (opener.position - 1)..(closer.position + 2),
                    type = ObsidianElementTypes.HIGHLIGHT,
                )
                result.withNode(node)
            }
        }
    }
}
