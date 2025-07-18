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
import com.pointlessapps.granite.markdown.renderer.styles.spans.BlockQuoteMarkdownSpanStyle
import com.pointlessapps.granite.markdown.renderer.styles.spans.CalloutMarkdownSpanStyle
import com.pointlessapps.granite.markdown.renderer.styles.spans.CodeBlockMarkdownSpanStyle
import com.pointlessapps.granite.markdown.renderer.styles.spans.CodeSpanMarkdownSpanStyle
import com.pointlessapps.granite.markdown.renderer.styles.spans.HashtagMarkdownSpanStyle
import com.pointlessapps.granite.markdown.renderer.styles.spans.HighlightMarkdownSpanStyle
import com.pointlessapps.granite.markdown.renderer.styles.spans.HorizontalRuleMarkdownSpanStyle

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
fun rememberMarkdownSpanStyles(): MarkdownSpanStyles {
    val context = LocalContext.current

    return remember {
        MarkdownSpanStyles(
            listOf(
                HighlightMarkdownSpanStyle,
                CodeSpanMarkdownSpanStyle,
                CodeBlockMarkdownSpanStyle,
                HashtagMarkdownSpanStyle,
                CalloutMarkdownSpanStyle(context),
                BlockQuoteMarkdownSpanStyle,
                HorizontalRuleMarkdownSpanStyle,
            ),
        )
    }
}
