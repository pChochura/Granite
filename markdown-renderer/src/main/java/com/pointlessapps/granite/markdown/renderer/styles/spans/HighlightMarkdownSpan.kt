package com.pointlessapps.granite.markdown.renderer.styles.spans

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import com.pointlessapps.granite.markdown.renderer.styles.HighlightSpanStyle
import com.pointlessapps.granite.markdown.renderer.styles.MarkdownSpanStyle
import com.pointlessapps.granite.markdown.renderer.styles.utils.getBoundingBoxes

class HighlightMarkdownSpan(
    private val style: HighlightSpanStyle,
) : MarkdownSpanStyle {

    companion object Companion {
        const val TAG_CONTENT = "HighlightMarkdownSpanStyle_Content"
    }

    private val path = Path()

    override fun prepare(
        result: TextLayoutResult,
        text: AnnotatedString,
    ): MarkdownSpanStyle.DrawInstruction {
        val cornerRadius = CornerRadius(style.cornerRadius)
        val annotations = text.getStringAnnotations(TAG_CONTENT, 0, text.length)

        path.reset()
        annotations.fastForEach { annotation ->
            val boxes = result.getBoundingBoxes(
                startOffset = annotation.start,
                endOffset = annotation.end,
            )
            boxes.fastForEachIndexed { index, box ->
                path.addRoundRect(
                    RoundRect(
                        rect = box.inflate(style.padding),
                        topLeft = if (index == 0) cornerRadius else CornerRadius.Zero,
                        bottomLeft = if (index == 0) cornerRadius else CornerRadius.Zero,
                        topRight = if (index == boxes.lastIndex) cornerRadius else CornerRadius.Zero,
                        bottomRight = if (index == boxes.lastIndex) cornerRadius else CornerRadius.Zero,
                    ),
                )
            }
        }

        return MarkdownSpanStyle.DrawInstruction {
            drawPath(path, style.backgroundColor)
        }
    }
}
