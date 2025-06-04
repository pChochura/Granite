package com.pointlessapps.obsidian_mini.markdown.renderer.providers

import androidx.compose.ui.text.AnnotatedString
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import org.intellij.markdown.IElementType

internal object DefaultStyleProvider : ProcessorStyleProvider {
    override fun styleNodeElement(
        element: NodeType,
        type: IElementType,
    ): List<AnnotatedString.Annotation> = emptyList()
}
