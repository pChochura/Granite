package com.pointlessapps.granite.markdown.renderer.processors

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import com.pointlessapps.granite.markdown.renderer.NodeProcessor
import com.pointlessapps.granite.markdown.renderer.models.ChildrenProcessing
import com.pointlessapps.granite.markdown.renderer.models.NodeMarker
import com.pointlessapps.granite.markdown.renderer.styles.spans.HorizontalRuleMarkdownSpan
import com.pointlessapps.granite.markdown.renderer.utils.withRange
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
            tag = HorizontalRuleMarkdownSpan.TAG_CONTENT,
        ),
    )

    override fun processChild(type: IElementType) = ChildrenProcessing.SKIP
}
