package com.pointlessapps.obsidian_mini.providers

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.em
import com.pointlessapps.obsidian_mini.NodeElement
import com.pointlessapps.obsidian_mini.NodeElement.LABEL
import com.pointlessapps.obsidian_mini.NodeElement.DECORATION
import com.pointlessapps.obsidian_mini.ProcessorStyleProvider
import org.intellij.markdown.IElementType

object FootnoteDefinitionStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeElement, type: IElementType?) = when (element) {
        LABEL, DECORATION -> listOf(
            SpanStyle(fontSize = 0.9.em, baselineShift = BaselineShift(0.2f)),
        )

        else -> listOf(SpanStyle(fontSize = 0.95.em))
    }
}
