package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.em
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement.DECORATION
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement.LABEL
import org.intellij.markdown.IElementType

object FootnoteLinkStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeElement, type: IElementType?) = when (element) {
        LABEL -> listOf(
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

        else -> throw IllegalArgumentException("FootnoteLinkStyleProvider doesn't style $element")
    }
}
