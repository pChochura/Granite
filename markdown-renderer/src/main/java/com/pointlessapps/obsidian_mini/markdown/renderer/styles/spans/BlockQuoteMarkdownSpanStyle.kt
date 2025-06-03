package com.pointlessapps.obsidian_mini.markdown.renderer.styles.spans

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastMapNotNull
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.MarkdownSpanStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.utils.getLinesBoundingBox

object BlockQuoteMarkdownSpanStyle : MarkdownSpanStyle {

    const val TAG_CONTENT = "BlockQuoteMarkdownSpanStyle_Content"

    private val path = Path()
    private val backgroundColor = Color(51, 51, 51)
    private val cornerRadius = 4.sp
    private val width = 4.sp

    override fun prepare(
        result: TextLayoutResult,
        text: AnnotatedString,
    ) = MarkdownSpanStyle.DrawInstruction {
        val cornerRadius = CornerRadius(cornerRadius.toPx())
        val annotations = text.getStringAnnotations(0, text.length)
            .fastFilter { it.item == TAG_CONTENT }

        annotations.fastMapNotNull { annotation ->
            val box = result.getLinesBoundingBox(
                startOffset = annotation.start,
                endOffset = annotation.end,
            )
            path.reset()
            path.addRoundRect(
                RoundRect(
                    rect = Rect(
                        top = box.top,
                        left = box.left,
                        bottom = box.bottom,
                        right = box.left + width.toPx(),
                    ),
                    cornerRadius = cornerRadius,
                ),
            )
            drawPath(path, backgroundColor)
        }
    }
}
