package com.pointlessapps.obsidian_mini

import org.intellij.markdown.ast.ASTNode

internal abstract class NodeProcessor(
    protected val styleProvider: ProcessorStyleProvider,
) {
    abstract fun processMarkers(node: ASTNode, textContent: String): Collection<NodeMarker>

    abstract fun processStyles(node: ASTNode, textContent: String): Collection<NodeStyle>

    abstract fun shouldProcessChildren(): Boolean
}
