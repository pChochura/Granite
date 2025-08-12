package com.pointlessapps.granite.markdown.renderer.styles.spans

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.util.fastForEach
import com.pointlessapps.granite.markdown.renderer.styles.HorizontalRuleSpanStyle
import com.pointlessapps.granite.markdown.renderer.styles.MarkdownSpanStyle
import com.pointlessapps.granite.markdown.renderer.styles.utils.getLinesBoundingBox

class HorizontalRuleMarkdownSpan(
    private val style: HorizontalRuleSpanStyle,
) : MarkdownSpanStyle {

    companion object {
        const val TAG_CONTENT = "HorizontalRuleMarkdownSpanStyle_Content"
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
            val box = result.getLinesBoundingBox(
                startOffset = annotation.start,
                endOffset = annotation.end,
            )
            path.addRoundRect(
                RoundRect(
                    rect = Rect(
                        left = box.left,
                        right = box.right,
                        top = box.top + (box.height - style.height) * 0.5f,
                        bottom = box.bottom - (box.height - style.height) * 0.5f,
                    ),
                    cornerRadius = cornerRadius,
                ),
            )
        }

        return MarkdownSpanStyle.DrawInstruction {
            drawPath(path, style.backgroundColor)
        }
    }
}
