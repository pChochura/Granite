package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastMap
import com.pointlessapps.markdown.obsidian.parser.obsidian.ObsidianElementTypes
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.models.toNodeStyles
import org.intellij.markdown.ast.ASTNode

internal class FootnoteDefinitionProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    override fun processMarkers(node: ASTNode) = node.children.fastMap {
        NodeMarker(it.startOffset, it.endOffset)
    }

    override fun processStyles(node: ASTNode): List<NodeStyle> {
        val idMarker = node.children.fastFirstOrNull { it.type == ObsidianElementTypes.FOOTNOTE_ID }
        val contentMarker = node.children.fastFirstOrNull {
            it.type == ObsidianElementTypes.FOOTNOTE_DEFINITION_TEXT
        }

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
