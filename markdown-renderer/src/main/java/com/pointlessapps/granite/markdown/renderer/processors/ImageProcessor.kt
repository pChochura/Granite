package com.pointlessapps.granite.markdown.renderer.processors

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.util.fastFirstOrNull
import com.pointlessapps.granite.markdown.renderer.NodeProcessor
import com.pointlessapps.granite.markdown.renderer.models.ChildrenProcessing
import com.pointlessapps.granite.markdown.renderer.models.NodeMarker
import com.pointlessapps.granite.markdown.renderer.utils.withRange
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal object ImageProcessor : NodeProcessor {

    override fun processMarkers(node: ASTNode): List<NodeMarker> {
        val linkNode = node.children.fastFirstOrNull { it.type == MarkdownElementTypes.INLINE_LINK }

        if (linkNode == null) {
            return emptyList()
        }

        val linkTextMarker = linkNode.children.fastFirstOrNull {
            it.type == MarkdownElementTypes.LINK_TEXT
        }
        val linkDestinationMarker = linkNode.children.fastFirstOrNull {
            it.type == MarkdownElementTypes.LINK_DESTINATION
        }

        if (linkTextMarker == null || linkDestinationMarker == null) {
            return emptyList()
        }

        val openingMarkers = listOfNotNull(
            linkTextMarker.children.fastFirstOrNull { it.type == MarkdownTokenTypes.LBRACKET },
            node.children.fastFirstOrNull { it.type == MarkdownTokenTypes.EXCLAMATION_MARK },
        )
        val closingMarker = linkTextMarker.children.fastFirstOrNull {
            it.type == MarkdownTokenTypes.RBRACKET
        }

        if (openingMarkers.isEmpty() || closingMarker == null) {
            return emptyList()
        }

        return listOf(
            NodeMarker(
                startOffset = openingMarkers.minOf { it.startOffset },
                endOffset = openingMarkers.maxOf { it.endOffset },
            ),
            NodeMarker(
                startOffset = closingMarker.startOffset,
                endOffset = closingMarker.endOffset,
            ),
            NodeMarker(
                startOffset = closingMarker.endOffset,
                endOffset = node.endOffset,
            ),
        )
    }

    override fun processStyles(node: ASTNode): List<AnnotatedString.Range<AnnotatedString.Annotation>> {
        val linkNode = node.children.fastFirstOrNull { it.type == MarkdownElementTypes.INLINE_LINK }

        if (linkNode == null) {
            return emptyList()
        }

        val linkTextMarker = linkNode.children.fastFirstOrNull {
            it.type == MarkdownElementTypes.LINK_TEXT
        }
        val linkDestinationMarker = linkNode.children.fastFirstOrNull {
            it.type == MarkdownElementTypes.LINK_DESTINATION
        }

        if (linkTextMarker == null || linkDestinationMarker == null) {
            return emptyList()
        }

        val openingMarkers = listOfNotNull(
            linkTextMarker.children.fastFirstOrNull { it.type == MarkdownTokenTypes.LBRACKET },
            node.children.fastFirstOrNull { it.type == MarkdownTokenTypes.EXCLAMATION_MARK },
        )
        val closingMarker = linkTextMarker.children.fastFirstOrNull {
            it.type == MarkdownTokenTypes.RBRACKET
        }

        if (openingMarkers.isEmpty() || closingMarker == null) {
            return emptyList()
        }

        return listOf(
            SpanStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline,
            ).withRange(
                start = node.startOffset,
                end = node.endOffset,
            ),
        )
    }

    override fun processChild(type: IElementType) = ChildrenProcessing.SKIP
}
