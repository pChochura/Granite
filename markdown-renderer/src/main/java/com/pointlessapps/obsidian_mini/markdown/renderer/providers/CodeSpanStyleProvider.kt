package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import org.intellij.markdown.IElementType

object CodeSpanStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeElement, type: IElementType?) = listOf(
        SpanStyle(
            background = Color.DarkGray.copy(alpha = 0.3f),
            fontFamily = FontFamily.Monospace,
        ),
    )
}
