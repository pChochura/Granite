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
import org.intellij.markdown.IElementType

object CalloutStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeType, type: IElementType) = listOfNotNull(
        when (element) {
            NodeType.Decoration -> SpanStyle(color = Color.DarkGray)
            is NodeType.Data -> SpanStyle(
                fontWeight = FontWeight.Bold,
                color = element.data.toColor(),
            )

            NodeType.All -> StringAnnotation(CalloutMarkdownSpanStyle.TAG_CONTENT)
            NodeType.Paragraph ->
                ParagraphStyle(
                    textIndent = TextIndent(
                        firstLine = 1.em,
                        restLine = 1.em,
                    ),
                    lineHeight = 1.6.em,
                    lineHeightStyle = LineHeightStyle.Default,
                )

            else -> null
        }
    )

    private fun String.toColor() = when (this) {
        "abstract", "summary", "tldr" -> Color(83, 223, 221)
        "info" -> Color(2, 122, 255)
        "todo" -> Color(2, 122, 255)
        "tip", "hint", "important" -> Color(83, 223, 221)
        "success", "check", "done" -> Color(68, 207, 110)
        "question", "help", "faq" -> Color(233, 151, 63)
        "warning", "caution", "attention" -> Color(233, 151, 63)
        "failure", "fail", "missing" -> Color(251, 70, 76)
        "danger", "error" -> Color(251, 70, 76)
        "bug" -> Color(251, 70, 76)
        "example" -> Color(168, 130, 255)
        "quote" -> Color(158, 158, 158)
        else -> Color(2, 122, 255)
    }
}