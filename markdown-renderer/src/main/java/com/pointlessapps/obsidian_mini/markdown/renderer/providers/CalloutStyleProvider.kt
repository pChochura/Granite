package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.StringAnnotation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.em
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.spans.CalloutMarkdownSpanStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.utils.CalloutTypes
import org.intellij.markdown.IElementType

object CalloutStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeType, type: IElementType) =
        when (element) {
            NodeType.Decoration -> listOf(SpanStyle(color = Color.DarkGray))
            is NodeType.Data -> listOf(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = CalloutTypes.getColor(element.data),
                ),
                StringAnnotation(element.data),
            )

            NodeType.All -> listOf(StringAnnotation(CalloutMarkdownSpanStyle.TAG_CONTENT))
            NodeType.Paragraph -> listOf(
                ParagraphStyle(
                    textIndent = TextIndent(
                        firstLine = 1.em,
                        restLine = 1.em,
                    ),
                    lineHeight = 1.6.em,
                    lineHeightStyle = LineHeightStyle.Default,
                ),
            )

            else -> emptyList()
        }
}
