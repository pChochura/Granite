package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import org.intellij.markdown.IElementType

object CommentBlockStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeElement, type: IElementType?) =
        listOf(SpanStyle(color = Color.Gray))
}
