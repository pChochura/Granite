package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.em
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType.CONTENT
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType.DECORATION
import org.intellij.markdown.IElementType

object InlineFootnoteStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeType, type: IElementType?) = when (element) {
        CONTENT -> listOf(
            SpanStyle(
                fontSize = 0.8.em,
                baselineShift = BaselineShift(0.4f),
            ),
        )

        DECORATION -> listOf(
            SpanStyle(
                fontSize = 0.8.em,
                baselineShift = BaselineShift(0.4f),
                color = Color.Gray,
            ),
        )

        else -> throw IllegalArgumentException("InlineFootnoteStyleProvider doesn't style $element")
    }
}
