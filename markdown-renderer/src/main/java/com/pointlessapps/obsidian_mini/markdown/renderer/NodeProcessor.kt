package com.pointlessapps.obsidian_mini.markdown.renderer

import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeStyle
import org.intellij.markdown.ast.ASTNode

internal abstract class NodeProcessor(
    protected val styleProvider: ProcessorStyleProvider,
) {
    /**
     * Processes a node and returns a set of markers that will be removed while rendering
     */
    abstract fun processMarkers(node: ASTNode): List<NodeMarker>

    /**
     * Processes a node and returns a set of styles for that node's text content
     */
    abstract fun processStyles(node: ASTNode): List<NodeStyle>

    /**
     * Same as [processStyles] with additional [textContent] of the whole document
     */
    open fun processStyles(node: ASTNode, textContent: String): List<NodeStyle> = processStyles(node)

    /**
     * Indicates whether this node's children should be styled as well
     */
    abstract fun shouldProcessChildren(): Boolean
}
