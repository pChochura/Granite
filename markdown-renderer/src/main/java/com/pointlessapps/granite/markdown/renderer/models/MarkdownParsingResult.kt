package com.pointlessapps.granite.markdown.renderer.models

import org.intellij.markdown.ast.ASTNode

internal data class MarkdownParsingResult(
    val rootNode: ASTNode,
    val textContent: String,
)
