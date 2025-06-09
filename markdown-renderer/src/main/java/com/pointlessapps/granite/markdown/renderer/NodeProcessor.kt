package com.pointlessapps.granite.markdown.renderer

import androidx.compose.ui.text.AnnotatedString
import com.pointlessapps.granite.markdown.renderer.models.ChildrenProcessing
import com.pointlessapps.granite.markdown.renderer.models.NodeMarker
import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode

/**
 * A structure that is being responsible for parsing and styling the [ASTNode].
 */
interface NodeProcessor {
    /**
     * Processes a node and returns a set of markers that will be removed while rendering.
     */
    fun processMarkers(node: ASTNode): List<NodeMarker>

    /**
     * Same as [processMarkers] with additional [textContent] of the whole document.
     */
    fun processMarkers(node: ASTNode, textContent: String): List<NodeMarker> = processMarkers(node)

    /**
     * Processes a node and returns a set of styles for that node's text content.
     */
    fun processStyles(node: ASTNode): List<AnnotatedString.Range<AnnotatedString.Annotation>>

    /**
     * Same as [processStyles] with additional [textContent] of the whole document.
     */
    fun processStyles(
        node: ASTNode,
        textContent: String,
    ): List<AnnotatedString.Range<AnnotatedString.Annotation>> = processStyles(node)

    /**
     * Indicates how this node's child of [type] should be processed.
     */
    fun processChild(type: IElementType): ChildrenProcessing
}
