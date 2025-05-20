package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.text.AnnotatedString
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import org.intellij.markdown.IElementType

object DefaultStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(
        element: NodeElement,
        type: IElementType?,
    ): List<AnnotatedString.Annotation> = emptyList()
}
