package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.providers.DefaultStyleProvider
import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode

internal object DefaultProcessor : NodeProcessor(DefaultStyleProvider) {

    override fun processMarkers(node: ASTNode): List<NodeMarker> = emptyList()

    override fun processStyles(node: ASTNode): List<NodeStyle> = emptyList()

    override fun shouldProcessChild(type: IElementType) = true
}