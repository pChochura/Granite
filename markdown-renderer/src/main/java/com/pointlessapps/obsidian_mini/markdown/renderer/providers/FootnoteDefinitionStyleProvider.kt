package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.em
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement.CONTENT
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement.DECORATION
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement.LABEL
import org.intellij.markdown.IElementType

object FootnoteDefinitionStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeElement, type: IElementType?) = when (element) {
        LABEL, DECORATION -> listOf(
            SpanStyle(fontSize = 0.9.em, baselineShift = BaselineShift(0.2f)),
        )

        CONTENT -> listOf(SpanStyle(fontSize = 0.95.em))

        else -> throw IllegalArgumentException("FootnoteDefinitionStyleProvider doesn't style $element")
    }
}
