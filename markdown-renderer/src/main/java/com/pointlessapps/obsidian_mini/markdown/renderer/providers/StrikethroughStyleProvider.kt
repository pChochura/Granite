package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import org.intellij.markdown.IElementType

object StrikethroughStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeElement, type: IElementType?) =
        listOf(SpanStyle(textDecoration = TextDecoration.LineThrough))
}
