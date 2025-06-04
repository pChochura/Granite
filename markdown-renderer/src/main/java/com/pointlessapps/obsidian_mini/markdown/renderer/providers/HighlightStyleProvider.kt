package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.StringAnnotation
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.spans.HighlightMarkdownSpanStyle
import org.intellij.markdown.IElementType

object HighlightStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeType, type: IElementType) = listOfNotNull(
        when (element) {
            NodeType.Decoration -> StringAnnotation(HighlightMarkdownSpanStyle.TAG_DELIMITER)
            NodeType.Content -> StringAnnotation(HighlightMarkdownSpanStyle.TAG_CONTENT)
            else -> null
        },
        SpanStyle(color = Color.Black),
    )
}
