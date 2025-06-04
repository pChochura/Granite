package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.StringAnnotation
import androidx.compose.ui.text.style.TextDecoration
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import org.intellij.markdown.IElementType

object InlineLinkStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(element: NodeType, type: IElementType) = listOf(
        when (element) {
            is NodeType.Clickable -> StringAnnotation(element.data)
            else -> SpanStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline,
            )
        },
    )
}
