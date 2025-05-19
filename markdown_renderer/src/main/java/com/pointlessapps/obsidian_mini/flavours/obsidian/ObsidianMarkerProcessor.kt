package com.pointlessapps.obsidian_mini.flavours.obsidian

import com.pointlessapps.obsidian_mini.flavours.obsidian.markerblocks.providers.FootnoteDefinitionProvider
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.flavours.commonmark.CommonMarkMarkerProcessor
import org.intellij.markdown.flavours.gfm.GFMConstraints
import org.intellij.markdown.flavours.gfm.GFMTokenTypes
import org.intellij.markdown.flavours.gfm.table.GitHubTableMarkerProvider
import org.intellij.markdown.parser.LookaheadText
import org.intellij.markdown.parser.MarkerProcessorFactory
import org.intellij.markdown.parser.ProductionHolder
import org.intellij.markdown.parser.constraints.CommonMarkdownConstraints
import org.intellij.markdown.parser.constraints.MarkdownConstraints
import org.intellij.markdown.parser.constraints.getCharsEaten
import org.intellij.markdown.parser.markerblocks.providers.AtxHeaderProvider
import org.intellij.markdown.parser.markerblocks.providers.BlockQuoteProvider
import org.intellij.markdown.parser.markerblocks.providers.CodeBlockProvider
import org.intellij.markdown.parser.markerblocks.providers.CodeFenceProvider
import org.intellij.markdown.parser.markerblocks.providers.HorizontalRuleProvider
import org.intellij.markdown.parser.markerblocks.providers.HtmlBlockProvider
import org.intellij.markdown.parser.markerblocks.providers.LinkReferenceDefinitionProvider
import org.intellij.markdown.parser.markerblocks.providers.ListMarkerProvider
import org.intellij.markdown.parser.markerblocks.providers.SetextHeaderProvider
import org.intellij.markdown.parser.sequentialparsers.SequentialParser
import kotlin.math.min

/**
 * An extension of [org.intellij.markdown.flavours.gfm.GFMMarkerProcessor]
 */
internal class ObsidianMarkerProcessor(
    productionHolder: ProductionHolder,
    constraintsBase: CommonMarkdownConstraints,
) : CommonMarkMarkerProcessor(productionHolder, constraintsBase) {

    override fun getMarkerBlockProviders() = listOf(
        CodeBlockProvider(),
        HorizontalRuleProvider(),
        CodeFenceProvider(),
        SetextHeaderProvider(),
        BlockQuoteProvider(),
        ListMarkerProvider(),
        AtxHeaderProvider(),
        HtmlBlockProvider(),
        // It has to be before LinkReferenceDefinitionProvider
        FootnoteDefinitionProvider(),
        LinkReferenceDefinitionProvider(),
        GitHubTableMarkerProvider(),
    )

    override fun populateConstraintsTokens(
        pos: LookaheadText.Position,
        constraints: MarkdownConstraints,
        productionHolder: ProductionHolder,
    ) {
        if (constraints !is GFMConstraints || !constraints.hasCheckbox()) {
            super.populateConstraintsTokens(pos, constraints, productionHolder)
            return
        }

        val line = pos.currentLine
        var offset = pos.offsetInCurrentLine
        while (offset < line.length && line[offset] != '[') {
            offset++
        }
        if (offset == line.length) {
            super.populateConstraintsTokens(pos, constraints, productionHolder)
            return
        }

        val type = when (constraints.types.lastOrNull()) {
            '>' ->
                MarkdownTokenTypes.BLOCK_QUOTE

            '.', ')' ->
                MarkdownTokenTypes.LIST_NUMBER

            else ->
                MarkdownTokenTypes.LIST_BULLET
        }
        val middleOffset = pos.offset - pos.offsetInCurrentLine + offset
        val endOffset = min(
            pos.offset - pos.offsetInCurrentLine + constraints.getCharsEaten(pos.currentLine),
            pos.nextLineOrEofOffset,
        )

        productionHolder.addProduction(
            listOf(
                SequentialParser.Node(pos.offset..middleOffset, type),
                SequentialParser.Node(middleOffset..endOffset, GFMTokenTypes.CHECK_BOX),
            ),
        )
    }

    object Factory : MarkerProcessorFactory {
        override fun createMarkerProcessor(productionHolder: ProductionHolder) =
            ObsidianMarkerProcessor(productionHolder, GFMConstraints.BASE)
    }
}
