package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.em
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMapIndexed
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.models.ChildrenProcessing
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.utils.atLineEnd
import com.pointlessapps.obsidian_mini.markdown.renderer.utils.atLineStart
import com.pointlessapps.obsidian_mini.markdown.renderer.utils.withRange
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal object UnorderedListProcessor : NodeProcessor {

    override fun processMarkers(node: ASTNode, textContent: String): List<NodeMarker> {
        val listItemMarkers = node.children.fastFilter {
            it.type == MarkdownElementTypes.LIST_ITEM
        }.fastFlatMap {
            it.children.fastFilter { it.type == MarkdownTokenTypes.LIST_BULLET }
        }

        if (listItemMarkers.isEmpty()) {
            return emptyList()
        }

        return listItemMarkers.fastMapIndexed { index, marker ->
            NodeMarker(
                startOffset = marker.startOffset,
                endOffset = marker.endOffset,
                replacement = "‣ ",
            )
        }
    }

    override fun processStyles(
        node: ASTNode,
        textContent: String,
    ): List<AnnotatedString.Range<AnnotatedString.Annotation>> {
        val listItemMarkers = node.children.fastFilter {
            it.type == MarkdownElementTypes.LIST_ITEM
        }.fastFlatMap {
            it.children.fastFilter { it.type == MarkdownTokenTypes.LIST_BULLET }
        }

        if (listItemMarkers.isEmpty()) {
            return emptyList()
        }

        return listOf(
            ParagraphStyle().withRange(
                start = node.startOffset.atLineStart(textContent),
                // Add an additional offset to make the paragraph render smoother
                end = node.endOffset.atLineEnd(textContent) + 1,
            ),
        ) + listItemMarkers.fastMap {
            SpanStyle(color = Color.DarkGray).withRange(
                start = it.startOffset,
                end = it.endOffset,
            )
        }
    }

    override fun processMarkers(node: ASTNode) =
        throw IllegalStateException("Could not process markers for the blockquote without the text content")

    override fun processStyles(node: ASTNode) =
        throw IllegalStateException("Could not process styles for the blockquote without the text content")

    override fun processChild(type: IElementType) = ChildrenProcessing.PROCESS_CHILDREN
}
