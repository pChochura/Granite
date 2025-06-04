package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.StringAnnotation
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.spans.HashtagMarkdownSpanStyle
import org.intellij.markdown.IElementType

object HashtagStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeType, type: IElementType?) = listOf(
        StringAnnotation(HashtagMarkdownSpanStyle.TAG_CONTENT),
        SpanStyle(color = Color.Black),
    )
}
