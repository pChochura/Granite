package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import com.pointlessapps.obsidian_mini.markdown.renderer.models.toNodeStyles
import org.intellij.markdown.ast.ASTNode

internal class HorizontalRuleProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    override fun processMarkers(node: ASTNode) = listOf(
        NodeMarker(
            startOffset = node.startOffset,
            endOffset = node.endOffset,
            // Make sure there is a character to display so that the SpanStyle has place to be
            // displayed
            replacement = " ",
        ),
    )

    override fun processStyles(node: ASTNode) =
        styleProvider.styleNodeElement(NodeType.All, node.type).toNodeStyles(
            startOffset = node.startOffset,
            endOffset = node.endOffset,
        )

    override fun shouldProcessChildren() = false
}
