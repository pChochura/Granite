package com.pointlessapps.obsidian_mini.providers

import androidx.compose.ui.text.AnnotatedString
import com.pointlessapps.obsidian_mini.models.NodeElement
import com.pointlessapps.obsidian_mini.ProcessorStyleProvider
import org.intellij.markdown.IElementType

object DefaultStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(
        element: NodeElement,
        type: IElementType?,
    ): List<AnnotatedString.Annotation> = emptyList()
}
