package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.StringAnnotation
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.em
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.spans.BlockQuoteMarkdownSpanStyle
import org.intellij.markdown.IElementType

object BlockQuoteStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeType, type: IElementType) = when (element) {
        NodeType.All -> listOf(
            StringAnnotation(BlockQuoteMarkdownSpanStyle.TAG_CONTENT),
            SpanStyle(fontStyle = FontStyle.Italic, color = Color.Cyan),
        )

        NodeType.Paragraph -> listOf(
            ParagraphStyle(
                textIndent = TextIndent(
                    firstLine = 1.em,
                    restLine = 1.em,
                ),
                lineHeight = 1.4.em,
                lineHeightStyle = LineHeightStyle.Default,
            )
        )

        else -> emptyList()
    }

}
