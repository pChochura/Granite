package com.pointlessapps.obsidian_mini.providers

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import com.pointlessapps.obsidian_mini.NodeElement
import com.pointlessapps.obsidian_mini.ProcessorStyleProvider
import org.intellij.markdown.IElementType

object ItalicStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeElement, type: IElementType?) =
        listOf(SpanStyle(fontStyle = FontStyle.Italic))
}
