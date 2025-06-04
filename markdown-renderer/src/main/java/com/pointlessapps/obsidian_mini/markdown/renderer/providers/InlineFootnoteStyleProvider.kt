package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.em
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import org.intellij.markdown.IElementType

object InlineFootnoteStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeType, type: IElementType) = when (element) {
        NodeType.Content -> listOf(
            SpanStyle(
                fontSize = 0.8.em,
                baselineShift = BaselineShift(0.4f),
            ),
        )

        NodeType.Decoration -> listOf(
            SpanStyle(
                fontSize = 0.8.em,
                baselineShift = BaselineShift(0.4f),
                color = Color.Gray,
            ),
        )

        else -> emptyList()
    }
}
