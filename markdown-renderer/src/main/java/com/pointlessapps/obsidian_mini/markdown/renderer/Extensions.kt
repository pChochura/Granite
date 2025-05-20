package com.pointlessapps.obsidian_mini.markdown.renderer

import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeProcessorResult
import org.intellij.markdown.ast.ASTNode

internal fun NodeProcessor.processNode(
    node: ASTNode,
    textContent: String,
    hideMarkers: Boolean,
) = NodeProcessorResult(
    styles = processStyles(node, textContent),
    markers = if (hideMarkers) processMarkers(node, textContent) else emptyList(),
    processChildren = shouldProcessChildren(),
)
