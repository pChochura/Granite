package com.pointlessapps.granite.markdown.renderer.processors

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import com.pointlessapps.granite.markdown.renderer.NodeProcessor
import com.pointlessapps.granite.markdown.renderer.models.ChildrenProcessing
import com.pointlessapps.granite.markdown.renderer.models.NodeMarker
import com.pointlessapps.granite.markdown.renderer.styles.spans.HashtagMarkdownSpan
import com.pointlessapps.granite.markdown.renderer.utils.withRange
import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

internal object HashtagProcessor : NodeProcessor {

    override fun processMarkers(node: ASTNode) =
        throw IllegalStateException("Could not process markers for the hashtag without the text content")

    override fun processMarkers(node: ASTNode, textContent: String) = listOf(
        NodeMarker(
            startOffset = node.startOffset,
            endOffset = node.endOffset,
            // Add additional spaces around the hashtag
            replacement = " ${node.getTextInNode(textContent)} ",
        ),
    )

    override fun processStyles(node: ASTNode) = listOf(
        SpanStyle(color = Color.Black).withRange(
            start = node.startOffset,
            end = node.endOffset,
            tag = HashtagMarkdownSpan.TAG_CONTENT,
        ),
    )

    override fun processChild(type: IElementType) = ChildrenProcessing.SKIP
}
