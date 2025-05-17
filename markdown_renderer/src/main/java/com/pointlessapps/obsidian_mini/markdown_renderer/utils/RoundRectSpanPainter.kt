package com.pointlessapps.obsidian_mini.markdown_renderer.utils

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach

/**
 * Draws round rectangles behind text annotated using `SpanStyle(background = …)`.
 *
 * [topMargin] and [bottomMargin] are placeholder values that will be automatically calculated from font metrics
 * in the future once Compose UI starts exposing them ([Issue tracker](https://issuetracker.google.com/u/1/issues/237428541)).
 * In the meantime, you can calculate these depending upon your text's font size and line height.
 */
class RoundRectSpanPainter(
    private val cornerRadius: TextUnit = 8.sp,
    private val stroke: Stroke? = null,
    private val padding: TextPaddingValues = TextPaddingValues(horizontal = 2.sp, vertical = 2.sp),
    private val topMargin: TextUnit,
    private val bottomMargin: TextUnit,
) : com.pointlessapps.obsidian_mini.markdown_renderer.utils.ExtendedSpanPainter() {
    private val path = Path()

    override fun decorate(
        span: SpanStyle,
        start: Int,
        end: Int,
        text: AnnotatedString,
        builder: AnnotatedString.Builder,
    ): SpanStyle {
        return if (span.background.isUnspecified) {
            span
        } else {
            builder.addStringAnnotation(
                TAG,
                annotation = span.background.serialize(),
                start = start,
                end = end,
            )
            span.copy(background = Color.Unspecified)
        }
    }

    override fun drawInstructionsFor(layoutResult: TextLayoutResult): com.pointlessapps.obsidian_mini.markdown_renderer.utils.SpanDrawInstructions {
        val text = layoutResult.layoutInput.text
        val annotations = text.getStringAnnotations(TAG, start = 0, end = text.length)

        return com.pointlessapps.obsidian_mini.markdown_renderer.utils.SpanDrawInstructions {
            val cornerRadius = CornerRadius(cornerRadius.toPx())

            annotations.fastForEach { annotation ->
                val backgroundColor = annotation.item.deserializeToColor()!!
                val boxes = layoutResult.getBoundingBoxes(
                    startOffset = annotation.start,
                    endOffset = annotation.end,
                    flattenForFullParagraphs = true,
                )
                boxes.forEachIndexed { index, box ->
                    path.reset()
                    path.addRoundRect(
                        RoundRect(
                            rect = box.copy(
                                left = box.left - padding.horizontal.toPx(),
                                right = box.right + padding.horizontal.toPx(),
                                top = box.top - padding.vertical.toPx() + topMargin.toPx(),
                                bottom = box.bottom + padding.vertical.toPx() - bottomMargin.toPx(),
                            ),
                            topLeft = if (index == 0) cornerRadius else CornerRadius.Zero,
                            bottomLeft = if (index == 0) cornerRadius else CornerRadius.Zero,
                            topRight = if (index == boxes.lastIndex) cornerRadius else CornerRadius.Zero,
                            bottomRight = if (index == boxes.lastIndex) cornerRadius else CornerRadius.Zero,
                        )
                    )
                    drawPath(
                        path = path,
                        color = backgroundColor,
                        style = Fill,
                    )
                    if (stroke != null) {
                        drawPath(
                            path = path,
                            color = stroke.color(backgroundColor),
                            style = Stroke(
                                width = stroke.width.toPx(),
                            ),
                        )
                    }
                }
            }
        }
    }

    data class Stroke(
        val color: (background: Color) -> Color,
        val width: TextUnit = 1.sp,
    ) {
        constructor(color: Color, width: TextUnit = 1.sp) : this(
            color = { color },
            width = width,
        )
    }

    data class TextPaddingValues(
        val horizontal: TextUnit = 0.sp,
        val vertical: TextUnit = 0.sp,
    )

    companion object {
        private const val TAG = "rounded_corner_span"
    }
}

private fun Color?.serialize(): String {
    return if (this == null || isUnspecified) "null" else "${toArgb()}"
}

private fun String.deserializeToColor(): Color? {
    return if (this == "null") null else Color(this.toInt())
}
