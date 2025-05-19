package com.pointlessapps.obsidian_mini.providers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import com.pointlessapps.obsidian_mini.NodeElement
import com.pointlessapps.obsidian_mini.ProcessorStyleProvider
import org.intellij.markdown.IElementType

object CodeSpanStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeElement, type: IElementType?) = listOf(
        SpanStyle(
            background = Color.DarkGray.copy(alpha = 0.3f),
            fontFamily = FontFamily.Monospace,
        ),
    )
}
