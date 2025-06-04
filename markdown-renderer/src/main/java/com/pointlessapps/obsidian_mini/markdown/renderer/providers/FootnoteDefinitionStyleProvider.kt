package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.em
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType.CONTENT
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType.DECORATION
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType.LABEL
import org.intellij.markdown.IElementType

object FootnoteDefinitionStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeType, type: IElementType) = when (element) {
        LABEL, DECORATION -> listOf(
            SpanStyle(fontSize = 0.9.em, baselineShift = BaselineShift(0.2f)),
        )

        CONTENT -> listOf(SpanStyle(fontSize = 0.95.em))

        else -> emptyList()
    }
}
