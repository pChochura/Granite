package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement.CONTENT
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement.DECORATION
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement.LABEL
import org.intellij.markdown.IElementType

object EmbedStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeElement, type: IElementType?) = when (element) {
        LABEL -> listOf(
            SpanStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold,
            ),
        )

        CONTENT, DECORATION -> listOf(
            SpanStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline,
            ),
        )

        else -> throw IllegalArgumentException("EmbedStyleProvider doesn't style $element")
    }
}
