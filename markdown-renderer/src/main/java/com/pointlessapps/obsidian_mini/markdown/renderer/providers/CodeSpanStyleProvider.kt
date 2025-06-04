package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.StringAnnotation
import androidx.compose.ui.text.font.FontFamily
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.spans.CodeSpanMarkdownSpanStyle
import org.intellij.markdown.IElementType

object CodeSpanStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeType, type: IElementType) = listOfNotNull(
        when (element) {
            NodeType.DECORATION -> StringAnnotation(CodeSpanMarkdownSpanStyle.TAG_DELIMITER)
            NodeType.CONTENT -> StringAnnotation(CodeSpanMarkdownSpanStyle.TAG_CONTENT)
            else -> null
        },
        SpanStyle(fontFamily = FontFamily.Monospace),
    )
}
