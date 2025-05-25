package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.em
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement
import org.intellij.markdown.IElementType

object BlockIdStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeElement, type: IElementType?) = listOf(
        SpanStyle(fontSize = 0.8.em, baselineShift = BaselineShift(0.4f)),
    )
}
