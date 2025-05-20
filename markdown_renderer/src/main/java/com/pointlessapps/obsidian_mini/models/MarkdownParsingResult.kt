package com.pointlessapps.obsidian_mini.models

import org.intellij.markdown.ast.ASTNode

internal data class MarkdownParsingResult(
    val rootNode: ASTNode,
    val textContent: String,
)
