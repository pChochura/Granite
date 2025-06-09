package com.pointlessapps.granite.markdown.renderer.processors

import androidx.compose.ui.text.AnnotatedString
import com.pointlessapps.granite.markdown.renderer.NodeProcessor
import com.pointlessapps.granite.markdown.renderer.models.ChildrenProcessing
import com.pointlessapps.granite.markdown.renderer.models.NodeMarker
import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode

internal object DefaultProcessor : NodeProcessor {

    override fun processMarkers(node: ASTNode): List<NodeMarker> = emptyList()

    override fun processStyles(node: ASTNode): List<AnnotatedString.Range<AnnotatedString.Annotation>> =
        emptyList()

    override fun processChild(type: IElementType) = ChildrenProcessing.PROCESS_CHILDREN
}
