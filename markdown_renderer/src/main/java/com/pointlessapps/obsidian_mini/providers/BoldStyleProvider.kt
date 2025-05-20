package com.pointlessapps.obsidian_mini.providers

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.pointlessapps.obsidian_mini.models.NodeElement
import com.pointlessapps.obsidian_mini.ProcessorStyleProvider
import org.intellij.markdown.IElementType

object BoldStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeElement, type: IElementType?) =
        listOf(SpanStyle(fontWeight = FontWeight.Bold))
}
