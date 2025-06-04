package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType.CONTENT
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType.DECORATION
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType.LABEL
import org.intellij.markdown.IElementType

object EmbedStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeType, type: IElementType) = when (element) {
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

        else -> emptyList()
    }
}
