package com.pointlessapps.obsidian_mini.flavours.obsidian.markerblocks

import com.pointlessapps.obsidian_mini.flavours.obsidian.ObsidianElementTypes
import org.intellij.markdown.parser.LookaheadText
import org.intellij.markdown.parser.ProductionHolder
import org.intellij.markdown.parser.constraints.MarkdownConstraints
import org.intellij.markdown.parser.markerblocks.MarkerBlock
import org.intellij.markdown.parser.markerblocks.MarkerBlockImpl

internal class FootnoteDefinitionMarkerBlock(
    myConstraints: MarkdownConstraints,
    marker: ProductionHolder.Marker,
    private val endPosition: Int,
) : MarkerBlockImpl(myConstraints, marker) {

    override fun allowsSubBlocks(): Boolean = false

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

    override fun getDefaultNodeType() = ObsidianElementTypes.FOOTNOTE_DEFINITION

    override fun isInterestingOffset(pos: LookaheadText.Position) = true
}
