package com.pointlessapps.obsidian_mini

import androidx.compose.ui.text.AnnotatedString
import org.intellij.markdown.IElementType

interface ProcessorStyleProvider {
    fun styleNodeElement(
        element: NodeElement,
        type: IElementType?,
    ): List<AnnotatedString.Annotation>
}
