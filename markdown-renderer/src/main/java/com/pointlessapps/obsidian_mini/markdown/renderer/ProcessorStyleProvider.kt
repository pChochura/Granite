package com.pointlessapps.obsidian_mini.markdown.renderer

import androidx.compose.ui.text.AnnotatedString
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import org.intellij.markdown.IElementType

interface ProcessorStyleProvider {
    fun styleNodeElement(
        element: NodeType,
        type: IElementType,
    ): List<AnnotatedString.Annotation>
}
