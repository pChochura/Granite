package com.pointlessapps.granite.markdown.renderer.styles.spans

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import com.pointlessapps.granite.markdown.renderer.styles.MarkdownSpanStyle
import com.pointlessapps.granite.markdown.renderer.styles.utils.getLinesBoundingBox

object HorizontalRuleMarkdownSpanStyle : MarkdownSpanStyle {

    const val TAG_CONTENT = "HorizontalRuleMarkdownSpanStyle_Content"

    private val path = Path()
    private val backgroundColor = Color(51, 51, 51)
    private val cornerRadius = 16.sp
    private val height = 4.sp

    override fun prepare(
        result: TextLayoutResult,
        text: AnnotatedString,
    ) = MarkdownSpanStyle.DrawInstruction {
        val cornerRadius = CornerRadius(cornerRadius.toPx())
        val annotations = text.getStringAnnotations(TAG_CONTENT, 0, text.length)

        annotations.fastForEach { annotation ->
            val box = result.getLinesBoundingBox(
                startOffset = annotation.start,
                endOffset = annotation.end,
            )
            path.reset()
            path.addRoundRect(
                RoundRect(
                    rect = Rect(
                        left = box.left,
                        right = box.right,
                        top = box.top + (box.height - height.toPx()) * 0.5f,
                        bottom = box.bottom - (box.height - height.toPx()) * 0.5f,
                    ),
                    cornerRadius = cornerRadius,
                ),
            )
            drawPath(path, backgroundColor)
        }
    }
}
