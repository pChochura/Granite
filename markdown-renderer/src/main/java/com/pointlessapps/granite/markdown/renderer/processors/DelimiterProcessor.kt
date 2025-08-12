package com.pointlessapps.granite.markdown.renderer.processors

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.pointlessapps.markdown.granite.parser.obsidian.ObsidianTokenTypes
import com.pointlessapps.granite.markdown.renderer.NodeProcessor
import com.pointlessapps.granite.markdown.renderer.models.ChildrenProcessing
import com.pointlessapps.granite.markdown.renderer.models.NodeMarker
import com.pointlessapps.granite.markdown.renderer.styles.spans.CodeSpanMarkdownSpan
import com.pointlessapps.granite.markdown.renderer.styles.spans.HighlightMarkdownSpan
import com.pointlessapps.granite.markdown.renderer.utils.withRange
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

    override fun processChild(type: IElementType) = ChildrenProcessing.PROCESS_CHILDREN
}

internal object BoldProcessor : DelimiterProcessor(
    styles = listOf(SpanStyle(fontWeight = FontWeight.Bold)),
    delimiter = MarkdownTokenTypes.EMPH,
)

internal object HighlightProcessor : DelimiterProcessor(
    styles = listOf(SpanStyle(color = Color.Black)),
    delimiter = ObsidianTokenTypes.EQ,
    tag = HighlightMarkdownSpan.TAG_CONTENT,
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
    tag = CodeSpanMarkdownSpan.TAG_CONTENT,
)
