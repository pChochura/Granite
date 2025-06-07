package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianTokenTypes
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.spans.CodeSpanMarkdownSpanStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.spans.HighlightMarkdownSpanStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.withRange
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMTokenTypes

internal open class DelimiterProcessor(
    private val styles: List<AnnotatedString.Annotation>,
    private val delimiter: IElementType,
    private val alwaysShowMarkers: Boolean = false,
    private val tag: String? = null,
) : NodeProcessor {

    override fun processMarkers(node: ASTNode): List<NodeMarker> {
        if (alwaysShowMarkers) return emptyList()

        val openingMarkers = node.children.takeWhile { it.type == delimiter }
        val closingMarkers = node.children.takeLastWhile { it.type == delimiter }

        if (openingMarkers.isEmpty() || closingMarkers.isEmpty() || openingMarkers.size != closingMarkers.size) {
            return emptyList()
        }

        // Flatten multiple subsequent markers into one
        return listOf(
            NodeMarker(
                startOffset = openingMarkers.minOf { it.startOffset },
                endOffset = openingMarkers.maxOf { it.endOffset },
            ),
            NodeMarker(
                startOffset = closingMarkers.minOf { it.startOffset },
                endOffset = closingMarkers.maxOf { it.endOffset },
            ),
        )
    }

    override fun processStyles(node: ASTNode): List<AnnotatedString.Range<AnnotatedString.Annotation>> {
        val openingMarkers = node.children.takeWhile { it.type == delimiter }
        val closingMarkers = node.children.takeLastWhile { it.type == delimiter }

        if (openingMarkers.isEmpty() || closingMarkers.isEmpty() || openingMarkers.size != closingMarkers.size) {
            return emptyList()
        }

        return styles.map { it.withRange(node.startOffset, node.endOffset, tag) }
    }

    override fun shouldProcessChild(type: IElementType) = true
}

internal object BoldProcessor : DelimiterProcessor(
    styles = listOf(SpanStyle(fontWeight = FontWeight.Bold)),
    delimiter = MarkdownTokenTypes.EMPH,
)

internal object HighlightProcessor : DelimiterProcessor(
    styles = listOf(SpanStyle(color = Color.Black)),
    delimiter = ObsidianTokenTypes.EQ,
    tag = HighlightMarkdownSpanStyle.TAG_CONTENT,
)

internal object ItalicProcessor : DelimiterProcessor(
    styles = listOf(SpanStyle(fontStyle = FontStyle.Italic)),
    delimiter = MarkdownTokenTypes.EMPH,
)

internal object StrikethroughProcessor : DelimiterProcessor(
    styles = listOf(SpanStyle(textDecoration = TextDecoration.LineThrough)),
    delimiter = GFMTokenTypes.TILDE,
)

internal object CommentProcessor : DelimiterProcessor(
    styles = listOf(SpanStyle(color = Color.Gray)),
    delimiter = ObsidianTokenTypes.PERCENT,
    alwaysShowMarkers = true,
)

internal object CodeSpanProcessor : DelimiterProcessor(
    styles = listOf(SpanStyle(fontFamily = FontFamily.Monospace)),
    delimiter = MarkdownTokenTypes.BACKTICK,
    tag = CodeSpanMarkdownSpanStyle.TAG_CONTENT,
)
