package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import org.intellij.markdown.ast.ASTNode

internal class DefaultProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    override fun processMarkers(node: ASTNode, textContent: String): List<NodeMarker> = emptyList()

    override fun processStyles(node: ASTNode, textContent: String): List<NodeStyle> = emptyList()

    override fun shouldProcessChildren() = true
}