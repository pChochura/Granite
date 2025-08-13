package com.pointlessapps.granite.markdown.renderer.processors

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import com.pointlessapps.granite.markdown.renderer.NodeProcessor
import com.pointlessapps.granite.markdown.renderer.models.ChildrenProcessing
import com.pointlessapps.granite.markdown.renderer.models.NodeMarker
import com.pointlessapps.granite.markdown.renderer.utils.withRange
import com.pointlessapps.markdown.granite.parser.obsidian.ObsidianTokenTypes
import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode

object CommentBlockProcessor : NodeProcessor {

    const val TAG = "TAG_CommentBlock"

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
                tag = TAG,
            ),
        )
    }

    override fun processChild(type: IElementType) = ChildrenProcessing.PROCESS_CHILDREN
}
