package com.pointlessapps.obsidian_mini.markdown.renderer

import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeStyle
import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode

internal interface NodeProcessor {
    /**
     * Processes a node and returns a set of markers that will be removed while rendering
     */
    fun processMarkers(node: ASTNode): List<NodeMarker>

    /**
     * Same as [processMarkers] with additional [textContent] of the whole document
     */
    fun processMarkers(node: ASTNode, textContent: String): List<NodeMarker> = processMarkers(node)

    /**
     * Processes a node and returns a set of styles for that node's text content
     */
    fun processStyles(node: ASTNode): List<NodeStyle>

    /**
     * Same as [processStyles] with additional [textContent] of the whole document
     */
    fun processStyles(node: ASTNode, textContent: String): List<NodeStyle> = processStyles(node)

    /**
     * Indicates whether this node's child of [type] should be styled as well
     */
    fun shouldProcessChild(type: IElementType): Boolean
}
