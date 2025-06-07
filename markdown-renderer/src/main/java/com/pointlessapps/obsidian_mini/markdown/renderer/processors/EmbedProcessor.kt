package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFirstOrNull
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.models.ChildrenProcessing
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.utils.withRange
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal object EmbedProcessor : NodeProcessor {

    override fun processMarkers(node: ASTNode): List<NodeMarker> {
        val exclamationMark = node.children.fastFirstOrNull {
            it.type == MarkdownTokenTypes.EXCLAMATION_MARK
        }
        val openingMarkers = node.children.fastFilter { it.type == MarkdownTokenTypes.LBRACKET }
        val closingMarkers = node.children.takeLastWhile { it.type == MarkdownTokenTypes.RBRACKET }

        if (
            exclamationMark == null ||
            openingMarkers.isEmpty() || closingMarkers.isEmpty() ||
            openingMarkers.size != closingMarkers.size
        ) {
            return emptyList()
        }

        val labelMarker = node.children.fastFirstOrNull {
            it.type == MarkdownElementTypes.LINK_LABEL
        }

        // Flatten multiple subsequent markers into one
        return listOfNotNull(
            NodeMarker(
                startOffset = exclamationMark.startOffset,
                endOffset = exclamationMark.endOffset,
            ),
            NodeMarker(
                startOffset = openingMarkers.minOf { it.startOffset },
                endOffset = openingMarkers.maxOf { it.endOffset },
            ),
            NodeMarker(
                startOffset = closingMarkers.minOf { it.startOffset },
                endOffset = closingMarkers.maxOf { it.endOffset },
            ),
            if (labelMarker != null) {
                NodeMarker(
                    startOffset = openingMarkers.maxOf { it.endOffset },
                    endOffset = labelMarker.startOffset,
                )
            } else null,
        )
    }

    override fun processStyles(node: ASTNode): List<AnnotatedString.Range<AnnotatedString.Annotation>> {
        val openingMarkers = node.children.takeWhile {
            it.type in listOf(MarkdownTokenTypes.EXCLAMATION_MARK, MarkdownTokenTypes.LBRACKET)
        }
        val closingMarkers = node.children.takeLastWhile { it.type == MarkdownTokenTypes.RBRACKET }

        if (openingMarkers.isEmpty() || closingMarkers.isEmpty()) {
            return emptyList()
        }

        val labelMarker = node.children.fastFirstOrNull {
            it.type == MarkdownElementTypes.LINK_LABEL
        }

        val style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)

        return listOfNotNull(
            style.withRange(
                start = openingMarkers.maxOf { it.endOffset },
                end = closingMarkers.minOf { it.startOffset },
            ),
            style.withRange(
                start = openingMarkers.minOf { it.startOffset },
                end = openingMarkers.maxOf { it.endOffset },
            ),
            style.withRange(
                start = closingMarkers.minOf { it.startOffset },
                end = closingMarkers.maxOf { it.endOffset },
            ),
            if (labelMarker != null) {
                style.copy(fontWeight = FontWeight.Bold).withRange(
                    start = labelMarker.startOffset,
                    end = labelMarker.endOffset,
                )
            } else null,
        )
    }

    override fun processChild(type: IElementType) = ChildrenProcessing.SKIP
}
