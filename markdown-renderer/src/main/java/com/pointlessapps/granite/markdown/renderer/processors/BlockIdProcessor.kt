package com.pointlessapps.granite.markdown.renderer.processors

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.em
import com.pointlessapps.granite.markdown.renderer.NodeProcessor
import com.pointlessapps.granite.markdown.renderer.models.ChildrenProcessing
import com.pointlessapps.granite.markdown.renderer.models.NodeMarker
import com.pointlessapps.granite.markdown.renderer.utils.withRange
import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode

object BlockIdProcessor : NodeProcessor {

    const val TAG = "TAG_BlockId"

    override fun processMarkers(node: ASTNode) = emptyList<NodeMarker>()

    override fun processStyles(node: ASTNode) = listOf(
        SpanStyle(fontSize = 0.8.em, baselineShift = BaselineShift(0.4f)).withRange(
            start = node.startOffset,
            end = node.endOffset,
            tag = TAG,
        ),
    )

    override fun processChild(type: IElementType) = ChildrenProcessing.SKIP
}
