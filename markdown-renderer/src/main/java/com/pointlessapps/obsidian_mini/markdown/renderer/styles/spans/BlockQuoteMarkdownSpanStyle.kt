package com.pointlessapps.obsidian_mini.markdown.renderer.styles.spans

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.MarkdownSpanStyle

object BlockQuoteMarkdownSpanStyle : MarkdownSpanStyle {

    const val TAG_CONTENT = "BlockQuoteMarkdownSpanStyle_Content"
    const val TAG_INDENT = "BlockQuoteMarkdownSpanStyle_Indent"

    private val path = Path()
    private val backgroundColor = Color(51, 51, 51)
    private val cornerRadius = 4.sp
    private val width = 2.sp
    private val horizontalPadding = 12.sp
    private val verticalPadding = 4.sp

    override fun prepare(
        result: TextLayoutResult,
        text: AnnotatedString,
    ) = MarkdownSpanStyle.DrawInstruction {
        val cornerRadius = CornerRadius(cornerRadius.toPx())
        val annotations = text.getStringAnnotations(TAG_CONTENT, 0, text.length)

        annotations.fastForEach { annotation ->
            val indentRegions = text.getStringAnnotations(
                tag = TAG_INDENT,
                start = annotation.start,
                end = annotation.end,
            )

            indentRegions.fastForEach { indent ->
                val firstLineIndex = result.getLineForOffset(indent.start)
                val lastLineIndex = result.getLineForOffset(indent.end)

                path.reset()
                path.addRoundRect(
                    RoundRect(
                        rect = Rect(
                            top = result.getLineTop(firstLineIndex) - verticalPadding.toPx(),
                            left = result.getHorizontalPosition(
                                offset = indent.start,
                                usePrimaryDirection = true,
                            ) - horizontalPadding.toPx(),
                            bottom = result.getLineBottom(lastLineIndex) + verticalPadding.toPx(),
                            right = result.getHorizontalPosition(
                                offset = indent.start,
                                usePrimaryDirection = true,
                            ) - horizontalPadding.toPx() + width.toPx(),
                        ),
                        cornerRadius = cornerRadius,
                    ),
                )
                drawPath(path, backgroundColor)
            }
        }
    }
}
