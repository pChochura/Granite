package com.pointlessapps.granite.markdown.renderer.styles.spans

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import com.pointlessapps.granite.markdown.renderer.processors.HashtagProcessor
import com.pointlessapps.granite.markdown.renderer.styles.HashtagSpanStyle
import com.pointlessapps.granite.markdown.renderer.styles.MarkdownSpanStyle
import com.pointlessapps.granite.markdown.renderer.styles.utils.getBoundingBoxes
import com.pointlessapps.granite.markdown.renderer.styles.utils.inflate

class HashtagMarkdownSpan(
    private val style: HashtagSpanStyle,
) : MarkdownSpanStyle {

    private val path = Path()

    override fun prepare(
        result: TextLayoutResult,
        text: AnnotatedString,
    ): MarkdownSpanStyle.DrawInstruction {
        val cornerRadius = CornerRadius(style.cornerRadius)
        val annotations = text.getStringAnnotations(HashtagProcessor.TAG, 0, text.length)

        path.reset()
        annotations.fastForEach { annotation ->
            val boxes = result.getBoundingBoxes(
                startOffset = annotation.start,
                endOffset = annotation.end,
            )
            boxes.fastForEachIndexed { index, box ->
                path.addRoundRect(
                    RoundRect(
                        rect = box.inflate(style.verticalPadding, style.horizontalPadding),
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
