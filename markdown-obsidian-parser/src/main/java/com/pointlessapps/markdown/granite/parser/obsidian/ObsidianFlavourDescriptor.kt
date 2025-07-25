package com.pointlessapps.markdown.granite.parser.obsidian

import com.pointlessapps.markdown.granite.parser.obsidian.lexer._ObsidianLexer
import com.pointlessapps.markdown.granite.parser.obsidian.parsers.BlockIdParser
import com.pointlessapps.markdown.granite.parser.obsidian.parsers.CommentDelimiterParser
import com.pointlessapps.markdown.granite.parser.obsidian.parsers.EmbedParser
import com.pointlessapps.markdown.granite.parser.obsidian.parsers.FootnoteLinkParser
import com.pointlessapps.markdown.granite.parser.obsidian.parsers.HashtagParser
import com.pointlessapps.markdown.granite.parser.obsidian.parsers.HighlightDelimiterParser
import com.pointlessapps.markdown.granite.parser.obsidian.parsers.InlineFootnoteParser
import com.pointlessapps.markdown.granite.parser.obsidian.parsers.InternalLinkParser
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMTokenTypes
import org.intellij.markdown.flavours.gfm.StrikeThroughDelimiterParser
import org.intellij.markdown.lexer.MarkdownLexer
import org.intellij.markdown.parser.MarkerProcessorFactory
import org.intellij.markdown.parser.sequentialparsers.EmphasisLikeParser
import org.intellij.markdown.parser.sequentialparsers.SequentialParserManager
import org.intellij.markdown.parser.sequentialparsers.impl.AutolinkParser
import org.intellij.markdown.parser.sequentialparsers.impl.BacktickParser
import org.intellij.markdown.parser.sequentialparsers.impl.EmphStrongDelimiterParser
import org.intellij.markdown.parser.sequentialparsers.impl.ImageParser
import org.intellij.markdown.parser.sequentialparsers.impl.InlineLinkParser
import org.intellij.markdown.parser.sequentialparsers.impl.MathParser

class ObsidianFlavourDescriptor(
    useSafeLinks: Boolean = true,
    absolutizeAnchorLinks: Boolean = false,
    makeHttpsAutoLinks: Boolean = false,
) : GFMFlavourDescriptor(useSafeLinks, absolutizeAnchorLinks, makeHttpsAutoLinks) {

    override val markerProcessorFactory: MarkerProcessorFactory = ObsidianMarkerProcessor.Factory

    override fun createInlinesLexer() = MarkdownLexer(_ObsidianLexer())

    override val sequentialParserManager = object : SequentialParserManager() {
        override fun getParserSequence() = listOf(
            AutolinkParser(listOf(MarkdownTokenTypes.AUTOLINK, GFMTokenTypes.GFM_AUTOLINK)),
            BlockIdParser(),
            InlineFootnoteParser(),
            BacktickParser(),
            MathParser(),
            ImageParser(),
            FootnoteLinkParser(),
            EmbedParser(),
            InternalLinkParser(),
            InlineLinkParser(),
            HashtagParser(),
            EmphasisLikeParser(
                CommentDelimiterParser(),
                EmphStrongDelimiterParser(),
                StrikeThroughDelimiterParser(),
                HighlightDelimiterParser(),
            ),
        )
    }
}
