package com.pointlessapps.granite.markdown.renderer.processors

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.StringAnnotation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastLastOrNull
import com.pointlessapps.granite.markdown.renderer.NodeProcessor
import com.pointlessapps.granite.markdown.renderer.models.ChildrenProcessing
import com.pointlessapps.granite.markdown.renderer.models.NodeMarker
import com.pointlessapps.granite.markdown.renderer.utils.atLineEnd
import com.pointlessapps.granite.markdown.renderer.utils.atLineStart
import com.pointlessapps.granite.markdown.renderer.utils.withRange
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import kotlin.math.max
import kotlin.math.min

object HeaderProcessor : NodeProcessor {

    const val TAG = "TAG_Header"

    private val headerStyles = listOf(
        SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
        ) to ParagraphStyle(lineHeight = 2.em),
        SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
        ) to ParagraphStyle(lineHeight = 1.74.em),
        SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
        ) to ParagraphStyle(lineHeight = 1.52.em),
        SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp,
        ) to ParagraphStyle(lineHeight = 1.32.em),
        SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        ) to ParagraphStyle(lineHeight = 1.15.em),
        SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        ) to ParagraphStyle(lineHeight = 1.em),
    )

    override fun processMarkers(node: ASTNode): List<NodeMarker> {
        val openingMarker = node.children.fastFirstOrNull {
            it.type == MarkdownTokenTypes.ATX_HEADER
        }

        if (openingMarker == null) {
            return emptyList()
        }

        val closingMarker = node.children.fastLastOrNull {
            it.type == MarkdownTokenTypes.ATX_HEADER
        }?.takeIf { it != openingMarker }

        // There's no content
        if (
            openingMarker.endOffset + 1 >= node.endOffset ||
            closingMarker != null && openingMarker.endOffset + 1 >= closingMarker.startOffset
        ) {
            return emptyList()
        }

        return buildList {
            add(
                NodeMarker(
                    startOffset = openingMarker.startOffset,
                    endOffset = min(openingMarker.endOffset + 1, node.endOffset),
                ),
            )

            if (closingMarker != null) {
                add(
                    NodeMarker(
                        startOffset = max(node.startOffset, closingMarker.startOffset - 1),
                        endOffset = closingMarker.endOffset,
                    ),
                )
            }
        }
    }

    override fun processStyles(
        node: ASTNode,
        textContent: String,
    ): List<AnnotatedString.Range<AnnotatedString.Annotation>> {
        val openingMarker = node.children.fastFirstOrNull {
            it.type == MarkdownTokenTypes.ATX_HEADER
        }

        if (openingMarker == null) {
            return emptyList()
        }

        val closingMarker = node.children.fastLastOrNull {
            it.type == MarkdownTokenTypes.ATX_HEADER
        }?.takeIf { it != openingMarker }

        val headingLevel = node.type.toHeadingLevel()
        return listOfNotNull(
            headerStyles[headingLevel].second.withRange(
                start = node.startOffset.atLineStart(textContent),
                // Add an additional offset to make the paragraph render smoother
                end = node.endOffset.atLineEnd(textContent) + 1,
            ),
            headerStyles[headingLevel].first.withRange(
                start = min(openingMarker.endOffset + 1, node.endOffset),
                end = node.endOffset,
            ),
            headerStyles[headingLevel].first.withRange(
                start = openingMarker.startOffset,
                end = min(openingMarker.endOffset + 1, node.endOffset),
            ),
            if (closingMarker != null) {
                headerStyles[headingLevel].first.withRange(
                    start = max(node.startOffset, closingMarker.startOffset - 1),
                    end = closingMarker.endOffset,
                )
            } else null,
            StringAnnotation("${headingLevel + 1}").withRange(
                start = node.startOffset,
                end = node.endOffset,
                tag = TAG,
            ),
        )
    }

    override fun processStyles(node: ASTNode) =
        throw IllegalStateException("Could not process styles for the header without the text content")

    override fun processChild(type: IElementType) = ChildrenProcessing.PROCESS_CHILDREN

    private fun IElementType.toHeadingLevel() = when (this) {
        MarkdownElementTypes.ATX_1 -> 0
        MarkdownElementTypes.ATX_2 -> 1
        MarkdownElementTypes.ATX_3 -> 2
        MarkdownElementTypes.ATX_4 -> 3
        MarkdownElementTypes.ATX_5 -> 4
        MarkdownElementTypes.ATX_6 -> 5
        else -> throw IllegalArgumentException("The provided IElementType is not a heading")
    }
}
