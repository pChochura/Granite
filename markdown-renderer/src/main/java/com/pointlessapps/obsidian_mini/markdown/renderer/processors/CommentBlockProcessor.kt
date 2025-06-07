package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianTokenTypes
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.models.ChildrenProcessing
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.utils.withRange
import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode

internal object CommentBlockProcessor : NodeProcessor {

    // Always show markers
    override fun processMarkers(node: ASTNode) = emptyList<NodeMarker>()

    override fun processStyles(node: ASTNode): List<AnnotatedString.Range<AnnotatedString.Annotation>> {
        val openingMarkers = node.children.takeWhile { it.type == ObsidianTokenTypes.PERCENT }
        val closingMarkers = node.children.takeLastWhile { it.type == ObsidianTokenTypes.PERCENT }

        if (openingMarkers.isEmpty() || closingMarkers.isEmpty()) {
            return emptyList()
        }

        return listOf(
            SpanStyle(color = Color.Gray).withRange(
                start = node.startOffset,
                end = node.endOffset,
            ),
        )
    }

    override fun processChild(type: IElementType) = ChildrenProcessing.PROCESS_CHILDREN
}
