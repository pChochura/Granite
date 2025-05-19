package com.pointlessapps.obsidian_mini.providers

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.em
import com.pointlessapps.obsidian_mini.NodeElement
import com.pointlessapps.obsidian_mini.ProcessorStyleProvider
import org.intellij.markdown.IElementType

object FootnoteLinkStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeElement, type: IElementType?) = listOf(
        SpanStyle(
            fontSize = 0.8.em,
            baselineShift = BaselineShift(0.4f),
            textDecoration = TextDecoration.Underline,
        ),
    )
}
