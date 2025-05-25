package com.pointlessapps.markdown.obsidian.parser.obsidian.markerblocks

import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.parser.LookaheadText
import org.intellij.markdown.parser.ProductionHolder
import org.intellij.markdown.parser.constraints.MarkdownConstraints
import org.intellij.markdown.parser.markerblocks.MarkerBlock
import org.intellij.markdown.parser.markerblocks.MarkerBlockImpl

internal class BlockQuoteMarkerBlock(
    myConstraints: MarkdownConstraints,
    marker: ProductionHolder.Marker,
    private val endPosition: Int,
) : MarkerBlockImpl(myConstraints, marker) {

    override fun allowsSubBlocks(): Boolean = true

    override fun getDefaultAction() = MarkerBlock.ClosingAction.DONE

    override fun doProcessToken(
        pos: LookaheadText.Position,
        currentConstraints: MarkdownConstraints,
    ): MarkerBlock.ProcessingResult {
        if (pos.offset < endPosition) {
            return MarkerBlock.ProcessingResult.CANCEL
        }

        return MarkerBlock.ProcessingResult.DEFAULT
    }

    override fun calcNextInterestingOffset(pos: LookaheadText.Position) = endPosition

    override fun getDefaultNodeType() = MarkdownElementTypes.BLOCK_QUOTE

    override fun isInterestingOffset(pos: LookaheadText.Position) = true
}
