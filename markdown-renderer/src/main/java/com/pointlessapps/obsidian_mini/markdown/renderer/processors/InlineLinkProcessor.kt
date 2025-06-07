package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.util.fastFirstOrNull
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.URL_TAG
import com.pointlessapps.obsidian_mini.markdown.renderer.models.ChildrenProcessing
import com.pointlessapps.obsidian_mini.markdown.renderer.utils.withRange
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

internal class InlineLinkProcessor(
    private val linkInteractionListener: LinkInteractionListener? = null,
) : NodeProcessor {

    override fun processMarkers(node: ASTNode): List<NodeMarker> {
        val linkTextMarker = node.children.fastFirstOrNull {
            it.type == MarkdownElementTypes.LINK_TEXT
        }
        val linkDestinationMarker = node.children.fastFirstOrNull {
            it.type == MarkdownElementTypes.LINK_DESTINATION
        }

        if (linkTextMarker == null || linkDestinationMarker == null) {
            return emptyList()
        }

        val openingMarker = linkTextMarker.children.fastFirstOrNull {
            it.type == MarkdownTokenTypes.LBRACKET
        }
        val closingMarker = linkTextMarker.children.fastFirstOrNull {
            it.type == MarkdownTokenTypes.RBRACKET
        }

        if (openingMarker == null || closingMarker == null) {
            return emptyList()
        }

        return listOf(
            NodeMarker(
                startOffset = openingMarker.startOffset,
                endOffset = openingMarker.endOffset,
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

    override fun processStyles(
        node: ASTNode,
        textContent: String,
    ): List<AnnotatedString.Range<AnnotatedString.Annotation>> {
        val linkTextMarker = node.children.fastFirstOrNull {
            it.type == MarkdownElementTypes.LINK_TEXT
        }
        val linkDestinationMarker = node.children.fastFirstOrNull {
            it.type == MarkdownElementTypes.LINK_DESTINATION
        }

        if (linkTextMarker == null || linkDestinationMarker == null) {
            return emptyList()
        }

        val openingTextMarker = linkTextMarker.children.fastFirstOrNull {
            it.type == MarkdownTokenTypes.LBRACKET
        }
        val closingTextMarker = linkTextMarker.children.fastFirstOrNull {
            it.type == MarkdownTokenTypes.RBRACKET
        }

        if (openingTextMarker == null || closingTextMarker == null) {
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
            LinkAnnotation.Url(
                url = linkDestinationMarker.getTextInNode(textContent).toString(),
                linkInteractionListener = linkInteractionListener,
            ).withRange(
                start = node.startOffset,
                end = node.endOffset,
                tag = URL_TAG,
            ),
        )
    }

    override fun processStyles(node: ASTNode) =
        throw IllegalStateException("Could not process styles for the internal link without the text content")

    override fun processChild(type: IElementType) = ChildrenProcessing.SKIP
}
