package com.pointlessapps.obsidian_mini.markdown.renderer.processors

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.em
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastMap
import com.pointlessapps.obsidian_mini.markdown.renderer.NodeProcessor
import com.pointlessapps.obsidian_mini.markdown.renderer.utils.atLineEnd
import com.pointlessapps.obsidian_mini.markdown.renderer.utils.atLineStart
import com.pointlessapps.obsidian_mini.markdown.renderer.models.NodeMarker
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.spans.CodeBlockMarkdownSpanStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.utils.withRange
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.ColorHighlight
import dev.snipme.highlights.model.SyntaxLanguage
import dev.snipme.highlights.model.SyntaxThemes
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal object CodeBlockProcessor : NodeProcessor {

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

    override fun processStyles(
        node: ASTNode,
        textContent: String,
    ): List<AnnotatedString.Range<AnnotatedString.Annotation>> {
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

        return listOfNotNull(
            ParagraphStyle(
                textIndent = TextIndent(
                    firstLine = 1.em,
                    restLine = 1.em,
                ),
                lineHeight = 1.4.em,
                lineHeightStyle = LineHeightStyle.Default,
            ).withRange(
                start = node.startOffset.atLineStart(textContent),
                // Add an additional offset to make the paragraph render smoother
                end = node.endOffset.atLineEnd(textContent) + 1,
            ),
            SpanStyle(fontFamily = FontFamily.Monospace).withRange(
                start = node.startOffset,
                end = node.endOffset,
                tag = CodeBlockMarkdownSpanStyle.TAG_CONTENT,
            ),
            if (langMarker != null) {
                SpanStyle(
                    fontSize = 0.6.em,
                    baselineShift = BaselineShift.Superscript,
                    color = Color(216, 67, 21, 255),
                    fontWeight = FontWeight.Bold,
                ).withRange(
                    start = langMarker.startOffset,
                    end = langMarker.endOffset,
                )
            } else null,
        ) + highlights.fastMap {
            SpanStyle(color = Color(it.rgb).copy(alpha = 1f)).withRange(
                start = node.startOffset + it.location.start,
                end = node.startOffset + it.location.end,
            )
        }
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
