package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import androidx.compose.ui.util.fastMapNotNull
import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianElementTypes
import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianTokenTypes
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.toNodeStyles
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal class FootnoteDefinitionProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    private companion object {
        const val NUMBER_OF_MARKERS = 4
    }

    override fun processMarkers(node: ASTNode) = node.children.fastMapNotNull {
        when (it.type) {
            MarkdownTokenTypes.LBRACKET -> NodeMarker(it.type, it.startOffset, it.endOffset)
            ObsidianTokenTypes.CARET -> NodeMarker(it.type, it.startOffset, it.endOffset)
            MarkdownTokenTypes.RBRACKET -> NodeMarker(it.type, it.startOffset, it.endOffset)
            MarkdownTokenTypes.COLON -> NodeMarker(it.type, it.startOffset, it.endOffset)
            else -> null
        }
    }.also {
        if (it.size != NUMBER_OF_MARKERS) throw IllegalStateException(
            "FootnoteDefinitionProcessor encountered incorrect amount of markers." +
                    "Expected: $NUMBER_OF_MARKERS, got: ${it.size}",
        )
    }

    override fun processStyles(node: ASTNode): List<NodeStyle> {
        val idMarker = node.children.find { it.type == ObsidianElementTypes.FOOTNOTE_ID }
        val contentMarker = node.children.find { it.type == ObsidianElementTypes.FOOTNOTE_DEFINITION_TEXT }

        if (idMarker == null || contentMarker == null) {
            throw IllegalStateException("FootnoteDefinitionProcessor encountered unbalanced amount of markers.")
        }

        return styleProvider.styleNodeElement(NodeElement.LABEL, node.type).toNodeStyles(
            startOffset = idMarker.startOffset,
            endOffset = idMarker.endOffset,
        ) + styleProvider.styleNodeElement(NodeElement.DECORATION, node.type).toNodeStyles(
            startOffset = node.startOffset,
            endOffset = idMarker.startOffset,
        ) + styleProvider.styleNodeElement(NodeElement.DECORATION, node.type).toNodeStyles(
            startOffset = idMarker.endOffset,
            endOffset = contentMarker.startOffset,
        ) + styleProvider.styleNodeElement(NodeElement.CONTENT, node.type).toNodeStyles(
            startOffset = contentMarker.startOffset,
            endOffset = contentMarker.endOffset,
        )
    }

    override fun shouldProcessChildren() = true
}
