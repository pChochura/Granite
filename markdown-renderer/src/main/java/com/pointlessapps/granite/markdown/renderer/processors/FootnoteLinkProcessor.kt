package com.pointlessapps.granite.markdown.renderer.processors

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.em
import androidx.compose.ui.util.fastFirstOrNull
import com.pointlessapps.markdown.granite.parser.obsidian.ObsidianTokenTypes
import com.pointlessapps.granite.markdown.renderer.NodeProcessor
import com.pointlessapps.granite.markdown.renderer.models.ChildrenProcessing
import com.pointlessapps.granite.markdown.renderer.models.NodeMarker
import com.pointlessapps.granite.markdown.renderer.utils.withRange
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal object FootnoteLinkProcessor : NodeProcessor {

    override fun processMarkers(node: ASTNode) = emptyList<NodeMarker>()

    override fun processStyles(node: ASTNode): List<AnnotatedString.Range<AnnotatedString.Annotation>> {
        val openingMarkers = node.children.takeWhile {
            it.type in listOf(MarkdownTokenTypes.LBRACKET, ObsidianTokenTypes.CARET)
        }
        val closingMarker = node.children.fastFirstOrNull { it.type == MarkdownTokenTypes.RBRACKET }

        if (openingMarkers.isEmpty() || closingMarker == null) {
            return emptyList()
        }

        val style = SpanStyle(fontSize = 0.8.em, baselineShift = BaselineShift(0.4f))

        return listOf(
            style.withRange(
                start = openingMarkers.maxOf { it.endOffset },
                end = closingMarker.startOffset,
            ),
            style.copy(color = Color.Gray).withRange(
                start = openingMarkers.minOf { it.startOffset },
                end = openingMarkers.maxOf { it.endOffset },
            ),
            style.copy(color = Color.Gray).withRange(
                start = closingMarker.startOffset,
                end = closingMarker.endOffset,
            ),
        )
    }

    override fun processChild(type: IElementType) = ChildrenProcessing.SKIP
}
