package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.em
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.withRange
import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode

internal object BlockIdProcessor : NodeProcessor {

    override fun processMarkers(node: ASTNode) = emptyList<NodeMarker>()

    override fun processStyles(node: ASTNode) = listOf(
        SpanStyle(fontSize = 0.8.em, baselineShift = BaselineShift(0.4f)).withRange(
            start = node.startOffset,
            end = node.endOffset,
        ),
    )

    override fun shouldProcessChild(type: IElementType) = false
}
