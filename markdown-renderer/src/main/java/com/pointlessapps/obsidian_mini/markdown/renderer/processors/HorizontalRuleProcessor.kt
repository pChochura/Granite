package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.spans.HorizontalRuleMarkdownSpanStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.withRange
import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode

internal object HorizontalRuleProcessor : NodeProcessor {

    override fun processMarkers(node: ASTNode) = listOf(
        NodeMarker(
            startOffset = node.startOffset,
            endOffset = node.endOffset,
            // Make sure there is a character to display so that the SpanStyle has place to be
            // displayed
            replacement = " ",
        ),
    )

    override fun processStyles(node: ASTNode) = listOf(
        SpanStyle(color = Color.Gray).withRange(
            start = node.startOffset,
            end = node.endOffset,
            tag = HorizontalRuleMarkdownSpanStyle.TAG_CONTENT,
        ),
    )

    override fun shouldProcessChild(type: IElementType) = false
}
