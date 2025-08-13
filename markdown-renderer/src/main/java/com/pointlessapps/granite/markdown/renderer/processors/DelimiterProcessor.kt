package com.pointlessapps.granite.markdown.renderer.processors

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.pointlessapps.granite.markdown.renderer.NodeProcessor
import com.pointlessapps.granite.markdown.renderer.models.ChildrenProcessing
import com.pointlessapps.granite.markdown.renderer.models.NodeMarker
import com.pointlessapps.granite.markdown.renderer.utils.withRange
import com.pointlessapps.markdown.granite.parser.obsidian.ObsidianTokenTypes
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMTokenTypes

open class DelimiterProcessor(
    private val style: AnnotatedString.Annotation,
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

        return listOf(style.withRange(node.startOffset, node.endOffset, tag))
    }

    override fun processChild(type: IElementType) = ChildrenProcessing.PROCESS_CHILDREN
}

object BoldProcessor : DelimiterProcessor(
    style = SpanStyle(fontWeight = FontWeight.Bold),
    delimiter = MarkdownTokenTypes.EMPH,
    tag = BoldProcessor.TAG,
) {
    const val TAG = "TAG_Bold"
}

object HighlightProcessor : DelimiterProcessor(
    style = SpanStyle(color = Color.Black),
    delimiter = ObsidianTokenTypes.EQ,
    tag = HighlightProcessor.TAG,
) {
    const val TAG = "TAG_Highlight"
}

object ItalicProcessor : DelimiterProcessor(
    style = SpanStyle(fontStyle = FontStyle.Italic),
    delimiter = MarkdownTokenTypes.EMPH,
    tag = ItalicProcessor.TAG,
) {
    const val TAG = "TAG_Italic"
}

object StrikethroughProcessor : DelimiterProcessor(
    style = SpanStyle(textDecoration = TextDecoration.LineThrough),
    delimiter = GFMTokenTypes.TILDE,
    tag = StrikethroughProcessor.TAG,
) {
    const val TAG = "TAG_Strikethrough"
}

object CommentProcessor : DelimiterProcessor(
    style = SpanStyle(color = Color.Gray),
    delimiter = ObsidianTokenTypes.PERCENT,
    alwaysShowMarkers = true,
    tag = CommentProcessor.TAG,
) {
    const val TAG = "TAG_Comment"
}

object CodeSpanProcessor : DelimiterProcessor(
    style = SpanStyle(fontFamily = FontFamily.Monospace),
    delimiter = MarkdownTokenTypes.BACKTICK,
    tag = CodeSpanProcessor.TAG,
) {
    const val TAG = "TAG_CodeSpan"
}
