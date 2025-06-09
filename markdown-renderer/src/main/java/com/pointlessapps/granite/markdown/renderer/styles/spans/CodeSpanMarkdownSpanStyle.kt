package com.pointlessapps.granite.markdown.renderer.styles.spans

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import com.pointlessapps.granite.markdown.renderer.styles.MarkdownSpanStyle
import com.pointlessapps.granite.markdown.renderer.styles.utils.getBoundingBoxes

object CodeSpanMarkdownSpanStyle : MarkdownSpanStyle {

    const val TAG_CONTENT = "CodeSpanMarkdownSpanStyle_Content"

    private val path = Path()
    private val backgroundColor = Color(59, 59, 59, 128)
    private val cornerRadius = 4.sp
    private val padding = 2.sp

    override fun prepare(
        result: TextLayoutResult,
        text: AnnotatedString,
    ) = MarkdownSpanStyle.DrawInstruction {
        val cornerRadius = CornerRadius(cornerRadius.toPx())
        val annotations = text.getStringAnnotations(TAG_CONTENT, 0, text.length)

        annotations.fastForEach { annotation ->
            val boxes = result.getBoundingBoxes(
                startOffset = annotation.start,
                endOffset = annotation.end,
            )
            boxes.fastForEachIndexed { index, box ->
                path.reset()
                path.addRoundRect(
                    RoundRect(
                        rect = box.inflate(padding.toPx()),
                        topLeft = if (index == 0) cornerRadius else CornerRadius.Zero,
                        bottomLeft = if (index == 0) cornerRadius else CornerRadius.Zero,
                        topRight = if (index == boxes.lastIndex) cornerRadius else CornerRadius.Zero,
                        bottomRight = if (index == boxes.lastIndex) cornerRadius else CornerRadius.Zero,
                    ),
                )
                drawPath(path, backgroundColor)
            }
        }
    }
}
