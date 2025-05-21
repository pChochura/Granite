package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement.CONTENT
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement.DECORATION
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement.PARAGRAPH
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
        element: NodeElement,
        type: IElementType?,
    ): List<AnnotatedString.Annotation> {
        if (type == null) {
            throw IllegalArgumentException(
                "HeaderStyleProvider styleNodeElement(): type cannot be null",
            )
        }

        return when (element) {
            PARAGRAPH -> listOf(headerStyles[type.toHeadingLevel()].second)
            CONTENT, DECORATION -> listOf(headerStyles[type.toHeadingLevel()].first)
            else -> throw IllegalArgumentException("HeaderStyleProvider doesn't style $element")
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
