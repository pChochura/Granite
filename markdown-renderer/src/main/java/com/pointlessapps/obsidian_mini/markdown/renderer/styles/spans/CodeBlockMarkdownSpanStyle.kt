package com.pointlessapps.obsidian_mini.markdown.renderer.styles.spans

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.MarkdownSpanStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.utils.getLinesBoundingBox

object CodeBlockMarkdownSpanStyle : MarkdownSpanStyle {

    const val TAG_CONTENT = "CodeBlockMarkdownSpanStyle_Content"

    private val path = Path()
    private val backgroundColor = Color(59, 59, 59, 128)
    private val backgroundCornerRadius = 4.sp
    private val backgroundPadding = 4.sp

    override fun prepare(
        result: TextLayoutResult,
        text: AnnotatedString,
    ) = MarkdownSpanStyle.DrawInstruction {
        text.getStringAnnotations(TAG_CONTENT, 0, text.length).fastForEach { annotation ->
            val box = result.getLinesBoundingBox(
                startOffset = annotation.start,
                endOffset = annotation.end,
            )
            path.reset()
            path.addRoundRect(
                RoundRect(
                    rect = box.inflate(backgroundPadding.toPx()),
                    cornerRadius = CornerRadius(backgroundCornerRadius.toPx()),
                ),
            )
            drawPath(path, backgroundColor)
        }
    }
}
