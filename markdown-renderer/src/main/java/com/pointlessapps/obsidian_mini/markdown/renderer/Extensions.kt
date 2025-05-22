package com.pointlessapps.obsidian_mini.markdown.renderer

import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeProcessorResult
import org.intellij.markdown.ast.ASTNode

internal fun NodeProcessor.processNode(
    node: ASTNode,
    hideMarkers: Boolean,
) = NodeProcessorResult(
    styles = processStyles(node),
    markers = if (hideMarkers) processMarkers(node) else emptyList(),
    processChildren = shouldProcessChildren(),
)
