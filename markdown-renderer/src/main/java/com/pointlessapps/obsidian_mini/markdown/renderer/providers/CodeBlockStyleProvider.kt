package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.StringAnnotation
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.em
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.spans.CodeBlockMarkdownSpanStyle
import org.intellij.markdown.IElementType

object CodeBlockStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeType, type: IElementType) = when (element) {
        NodeType.LABEL -> listOf(
            SpanStyle(
                fontSize = 0.6.em,
                baselineShift = BaselineShift.Superscript,
                color = Color(216, 67, 21, 255),
                fontWeight = FontWeight.Bold,
            ),
        )

        NodeType.WHOLE_NODE -> listOf(
            StringAnnotation(CodeBlockMarkdownSpanStyle.TAG_CONTENT),
            SpanStyle(fontFamily = FontFamily.Monospace),
        )

        NodeType.PARAGRAPH -> listOf(
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
