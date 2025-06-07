package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.em
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastMapNotNull
import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianElementTypes
import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianTokenTypes
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.utils.withRange
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal object FootnoteDefinitionProcessor : NodeProcessor {

    override fun processMarkers(node: ASTNode) = node.children.fastMapNotNull {
        if (
            it.type in listOf(
                MarkdownTokenTypes.LBRACKET,
                ObsidianTokenTypes.CARET,
                MarkdownTokenTypes.RBRACKET,
                MarkdownTokenTypes.COLON,
            )
        ) {
            NodeMarker(it.startOffset, it.endOffset)
        } else {
            null
        }
    } + NodeMarker(
        startOffset = node.endOffset,
        endOffset = node.endOffset,
        replacement = "↩",
    )

    override fun processStyles(node: ASTNode): List<AnnotatedString.Range<AnnotatedString.Annotation>> {
        val idMarker = node.children.fastFirstOrNull { it.type == ObsidianElementTypes.FOOTNOTE_ID }
        val contentMarker = node.children.fastFirstOrNull {
            it.type == ObsidianElementTypes.FOOTNOTE_DEFINITION_TEXT
        }

        if (idMarker == null || contentMarker == null) {
            return emptyList()
        }

        val style = SpanStyle(fontSize = 0.9.em, baselineShift = BaselineShift(0.2f))

        return listOf(
            style.withRange(
                start = idMarker.startOffset,
                end = idMarker.endOffset,
            ),
            style.withRange(
                start = node.startOffset,
                end = idMarker.startOffset,
            ),
            style.withRange(
                start = idMarker.endOffset,
                end = contentMarker.startOffset,
            ),
            SpanStyle(fontSize = 0.95.em).withRange(
                start = contentMarker.startOffset,
                end = contentMarker.endOffset,
            ),
        )
    }

    override fun shouldProcessChild(type: IElementType) = true
}
