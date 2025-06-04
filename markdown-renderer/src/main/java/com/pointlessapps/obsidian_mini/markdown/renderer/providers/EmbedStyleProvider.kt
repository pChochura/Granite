package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import org.intellij.markdown.IElementType

object EmbedStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeType, type: IElementType) = when (element) {
        NodeType.Label -> listOf(
            SpanStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold,
            ),
        )

        NodeType.Content, NodeType.Decoration -> listOf(
            SpanStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline,
            ),
        )

        else -> emptyList()
    }
}
