package com.pointlessapps.granite.markdown.renderer.styles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import com.pointlessapps.granite.markdown.renderer.styles.spans.BlockQuoteMarkdownSpan
import com.pointlessapps.granite.markdown.renderer.styles.spans.CalloutMarkdownSpan
import com.pointlessapps.granite.markdown.renderer.styles.spans.CodeBlockMarkdownSpan
import com.pointlessapps.granite.markdown.renderer.styles.spans.CodeSpanMarkdownSpan
import com.pointlessapps.granite.markdown.renderer.styles.spans.HashtagMarkdownSpan
import com.pointlessapps.granite.markdown.renderer.styles.spans.HighlightMarkdownSpan
import com.pointlessapps.granite.markdown.renderer.styles.spans.HorizontalRuleMarkdownSpan

/**
 * A helper class to handle preparing and delegating the drawing process to [spanStyles].
 * Each [MarkdownSpanStyle] contain instructions on how to draw itself.
 */
class MarkdownSpanStyles(private val spanStyles: List<MarkdownSpanStyle>) {

    private var spanStylesDrawInstructions = emptyList<MarkdownSpanStyle.DrawInstruction>()

    fun DrawScope.draw() {
        spanStylesDrawInstructions.fastForEach { with(it) { draw() } }
    }

    fun update(
        result: TextLayoutResult,
        text: AnnotatedString,
    ) {
        spanStylesDrawInstructions = spanStyles.fastMap {
            it.prepare(result, text)
        }
    }
}

fun Modifier.draw(markdownSpanStyles: MarkdownSpanStyles) = drawWithCache {
    onDrawBehind { with(markdownSpanStyles) { draw() } }
}

@Composable
fun rememberMarkdownSpanStyles(
    spanStyles: List<MarkdownSpanStyle> = listOf(
        HighlightMarkdownSpan(HighlightSpanStyle.defaultStyle()),
        BlockQuoteMarkdownSpan(BlockQuoteSpanStyle.defaultStyle()),
        CodeBlockMarkdownSpan(CodeBlockSpanStyle.defaultStyle()),
        CodeSpanMarkdownSpan(CodeSpanSpanStyle.defaultStyle()),
        HashtagMarkdownSpan(HashtagSpanStyle.defaultStyle()),
        HorizontalRuleMarkdownSpan(HorizontalRuleSpanStyle.defaultStyle()),
        CalloutMarkdownSpan(CalloutSpanStyle.defaultStyle(), LocalContext.current),
    )
): MarkdownSpanStyles = remember { MarkdownSpanStyles(spanStyles) }
