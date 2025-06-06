package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastMap
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.ProcessorStyleProvider
import com.pointlessapps.obsidian_mini.markdown.renderer.atLineEnd
import com.pointlessapps.obsidian_mini.markdown.renderer.atLineStart
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeType
import com.pointlessapps.obsidian_mini.markdown.renderer.models.toNodeStyles
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.ColorHighlight
import dev.snipme.highlights.model.SyntaxLanguage
import dev.snipme.highlights.model.SyntaxThemes
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal class CodeBlockProcessor(
    styleProvider: ProcessorStyleProvider,
) : NodeProcessor(styleProvider) {

    override fun processMarkers(node: ASTNode): List<NodeMarker> {
        val fenceMarkers = node.children.fastFilter {
            it.type == MarkdownTokenTypes.CODE_FENCE_START ||
                    it.type == MarkdownTokenTypes.CODE_FENCE_END
        }

        if (fenceMarkers.isEmpty()) {
            return emptyList()
        }

        return fenceMarkers.fastMap {
            NodeMarker(
                startOffset = it.startOffset,
                endOffset = it.endOffset,
            )
        }
    }

    override fun processStyles(node: ASTNode, textContent: String): List<NodeStyle> {
        val langMarker = node.children.fastFirstOrNull { it.type == MarkdownTokenTypes.FENCE_LANG }

        val highlights = Highlights.Builder()
            .language(
                langMarker?.let {
                    textContent.substring(it.startOffset, it.endOffset)
                }.toLang(),
            )
            .theme(SyntaxThemes.pastel(true))
            .code(textContent.substring(node.startOffset, node.endOffset))
            .build()
            .getHighlights()
            .filterIsInstance<ColorHighlight>()

        return styleProvider.styleNodeElement(NodeType.Paragraph, node.type).toNodeStyles(
            startOffset = node.startOffset.atLineStart(textContent),
            // Add an additional offset to make the paragraph render smoother
            endOffset = node.endOffset.atLineEnd(textContent) + 1,
        ) + styleProvider.styleNodeElement(NodeType.All, node.type).toNodeStyles(
            startOffset = node.startOffset,
            endOffset = node.endOffset,
        ) + highlights.fastMap {
            NodeStyle(
                annotation = SpanStyle(color = Color(it.rgb).copy(alpha = 1f)),
                startOffset = node.startOffset + it.location.start,
                endOffset = node.startOffset + it.location.end,
            )
        } + if (langMarker != null) {
            styleProvider.styleNodeElement(NodeType.Label, node.type).toNodeStyles(
                startOffset = langMarker.startOffset,
                endOffset = langMarker.endOffset,
            )
        } else emptyList()
    }

    override fun processStyles(node: ASTNode) =
        throw IllegalStateException("Could not process styles for the code block without the text content")

    override fun shouldProcessChild(type: IElementType) = false

    private fun String?.toLang() = when (this) {
        "c" -> SyntaxLanguage.C
        "c++", "cpp" -> SyntaxLanguage.CPP
        "dart" -> SyntaxLanguage.DART
        "java" -> SyntaxLanguage.JAVA
        "kotlin", "kt" -> SyntaxLanguage.KOTLIN
        "rust", "rs" -> SyntaxLanguage.RUST
        "c#", "csharp", "c-sharp" -> SyntaxLanguage.CSHARP
        "coffescript", "coffe" -> SyntaxLanguage.COFFEESCRIPT
        "js", "javascript" -> SyntaxLanguage.JAVASCRIPT
        "pl", "perl" -> SyntaxLanguage.PERL
        "py", "python" -> SyntaxLanguage.PYTHON
        "r", "ruby" -> SyntaxLanguage.RUBY
        "$", "shell" -> SyntaxLanguage.SHELL
        "swift" -> SyntaxLanguage.SWIFT
        "ts", "typescript" -> SyntaxLanguage.TYPESCRIPT
        "go" -> SyntaxLanguage.GO
        "php" -> SyntaxLanguage.PHP
        else -> SyntaxLanguage.DEFAULT
    }
}
