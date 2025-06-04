package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastMapNotNull
import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianElementTypes
import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianTokenTypes
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.models.toNodeStyles
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal class FootnoteDefinitionProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    override fun processMarkers(node: ASTNode) = node.children.fastMapNotNull {
        if (
            it.type in listOf(
                MarkdownTokenTypes.LBRACKET,
                ObsidianTokenTypes.CARET,
                MarkdownTokenTypes.RBRACKET,
                MarkdownTokenTypes.COLON,
            )
        ) {
            NodeMarker(it.startOffset, it.endOffset)
        } else {
            null
        }
    } + NodeMarker(
        startOffset = node.endOffset,
        endOffset = node.endOffset,
        replacement = "↩",
    )

    override fun processStyles(node: ASTNode): List<NodeStyle> {
        val idMarker = node.children.fastFirstOrNull { it.type == ObsidianElementTypes.FOOTNOTE_ID }
        val contentMarker = node.children.fastFirstOrNull {
            it.type == ObsidianElementTypes.FOOTNOTE_DEFINITION_TEXT
        }

        if (idMarker == null || contentMarker == null) {
            return emptyList()
        }

        return styleProvider.styleNodeElement(NodeType.Label, node.type).toNodeStyles(
            startOffset = idMarker.startOffset,
            endOffset = idMarker.endOffset,
        ) + styleProvider.styleNodeElement(NodeType.Decoration, node.type).toNodeStyles(
            startOffset = node.startOffset,
            endOffset = idMarker.startOffset,
        ) + styleProvider.styleNodeElement(NodeType.Decoration, node.type).toNodeStyles(
            startOffset = idMarker.endOffset,
            endOffset = contentMarker.startOffset,
        ) + styleProvider.styleNodeElement(NodeType.Content, node.type).toNodeStyles(
            startOffset = contentMarker.startOffset,
            endOffset = contentMarker.endOffset,
        )
    }

    override fun shouldProcessChildren() = true
}
