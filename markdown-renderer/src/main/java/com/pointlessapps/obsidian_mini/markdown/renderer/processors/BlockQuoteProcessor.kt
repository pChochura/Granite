package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeElement
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.models.toNodeStyles
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal class BlockQuoteProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    override fun processMarkers(node: ASTNode, textContent: String): List<NodeMarker> {
        val numberOfLines = textContent.lines().size
        val markers = ArrayList<NodeMarker>(numberOfLines)

        var index = 0
        while (index < textContent.lastIndex) {
            if (textContent[index] == '>' && textContent.getOrNull(index + 1) == ' ') {
                markers.add(
                    NodeMarker(
                        element = MarkdownTokenTypes.GT,
                        startOffset = node.startOffset + index,
                        endOffset = node.startOffset + index + 2,
                    ),
                )

                // Advance after the whitespace
                index++
            }

            index++
        }

        if (markers.size != numberOfLines) {
            throw IllegalStateException("BlockQuoteProcessor encountered unbalanced amount of markers.")
        }

        return markers
    }

    override fun processStyles(node: ASTNode, textContent: String): List<NodeStyle> {
        val numberOfLines = textContent.lines().size
        val styles = ArrayList<NodeStyle>(numberOfLines * 2)

        var index = 0
        var contentStartIndex = -1
        while (index <= textContent.lastIndex) {
            if (textContent[index] == '>' && textContent.getOrNull(index + 1) == ' ') {
                styles.addAll(
                    styleProvider.styleNodeElement(NodeElement.DECORATION, node.type).toNodeStyles(
                        startOffset = node.startOffset + index,
                        endOffset = node.startOffset + index + 2,
                    ),
                )

                if (contentStartIndex != -1) {
                    styles.addAll(
                        styleProvider.styleNodeElement(NodeElement.CONTENT, node.type).toNodeStyles(
                            startOffset = contentStartIndex,
                            endOffset = node.startOffset + index - 1, // The end of the previous line
                        ),
                    )
                }

                // Reset the contentStartIndex because a new line is starting
                contentStartIndex = node.startOffset + index + 2

                // Advance after the whitespace
                index++
            }

            index++
        }

        if (contentStartIndex != -1) {
            styles.addAll(
                styleProvider.styleNodeElement(NodeElement.CONTENT, node.type).toNodeStyles(
                    startOffset = contentStartIndex,
                    endOffset = node.endOffset, // The end of the node
                ),
            )
        }

        return styles
    }

    override fun shouldProcessChildren() = true
}
