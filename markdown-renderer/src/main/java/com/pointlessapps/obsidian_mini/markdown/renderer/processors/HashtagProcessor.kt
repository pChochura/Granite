package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import com.pointlessapps.obsidian_mini.markdown.renderer.models.toNodeStyles
import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

internal class HashtagProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    override fun processMarkers(node: ASTNode) =
        throw IllegalStateException("Could not process markers for the hashtag without the text content")

    override fun processMarkers(node: ASTNode, textContent: String) = listOf(
        NodeMarker(
            startOffset = node.startOffset,
            endOffset = node.endOffset,
            // Add additional spaces around the hashtag
            replacement = " ${node.getTextInNode(textContent)} ",
        ),
    )

    override fun processStyles(node: ASTNode) =
        styleProvider.styleNodeElement(NodeType.Content, node.type).toNodeStyles(
            startOffset = node.startOffset,
            endOffset = node.endOffset,
        )

    override fun shouldProcessChild(type: IElementType) = false
}
