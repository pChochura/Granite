package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes

object HeaderStyleProvider : ProcessorStyleProvider {

    private val headerStyles = listOf(
        SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
        ) to ParagraphStyle(lineHeight = 2.em),
        SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
        ) to ParagraphStyle(lineHeight = 1.74.em),
        SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
        ) to ParagraphStyle(lineHeight = 1.52.em),
        SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp,
        ) to ParagraphStyle(lineHeight = 1.32.em),
        SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        ) to ParagraphStyle(lineHeight = 1.15.em),
        SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        ) to ParagraphStyle(lineHeight = 1.em),
    )

    override fun styleNodeElement(
        element: NodeType,
        type: IElementType,
    ): List<AnnotatedString.Annotation> {
        return when (element) {
            NodeType.Paragraph -> listOf(headerStyles[type.toHeadingLevel()].second)
            NodeType.Content, NodeType.Decoration -> listOf(headerStyles[type.toHeadingLevel()].first)
            else -> emptyList()
        }
    }

    private fun IElementType.toHeadingLevel() = when (this) {
        MarkdownElementTypes.ATX_1 -> 0
        MarkdownElementTypes.ATX_2 -> 1
        MarkdownElementTypes.ATX_3 -> 2
        MarkdownElementTypes.ATX_4 -> 3
        MarkdownElementTypes.ATX_5 -> 4
        MarkdownElementTypes.ATX_6 -> 5
        else -> throw IllegalArgumentException("The provided IElementType is not a heading")
    }
}
