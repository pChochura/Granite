package com.pointlessapps.obsidian_mini.providers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import com.pointlessapps.obsidian_mini.NodeElement
import com.pointlessapps.obsidian_mini.ProcessorStyleProvider
import org.intellij.markdown.IElementType

object InternalLinkStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeElement, type: IElementType?) = listOf(
        SpanStyle(
            color = Color.Blue,
            textDecoration = TextDecoration.Underline,
        ),
    )
}
