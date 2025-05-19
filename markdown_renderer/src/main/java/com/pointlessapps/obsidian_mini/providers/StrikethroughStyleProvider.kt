package com.pointlessapps.obsidian_mini.providers

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import com.pointlessapps.obsidian_mini.NodeElement
import com.pointlessapps.obsidian_mini.ProcessorStyleProvider
import org.intellij.markdown.IElementType

object StrikethroughStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeElement, type: IElementType?) =
        listOf(SpanStyle(textDecoration = TextDecoration.LineThrough))
}
