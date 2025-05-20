package com.pointlessapps.obsidian_mini.providers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import com.pointlessapps.obsidian_mini.models.NodeElement
import com.pointlessapps.obsidian_mini.ProcessorStyleProvider
import org.intellij.markdown.IElementType

object BlockQuoteStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeElement, type: IElementType?) =
        listOf(SpanStyle(background = Color.DarkGray.copy(alpha = 0.3f)))
}
