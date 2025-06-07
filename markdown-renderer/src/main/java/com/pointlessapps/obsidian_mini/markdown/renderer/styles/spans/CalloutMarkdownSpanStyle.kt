package com.pointlessapps.obsidian_mini.markdown.renderer.styles.spans

import android.content.Context
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.MarkdownSpanStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.utils.CalloutTypes
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.utils.getLinesBoundingBox

class CalloutMarkdownSpanStyle(
    private val context: Context,
) : MarkdownSpanStyle {

    companion object {
        const val TAG_CONTENT = "CalloutMarkdownSpanStyle_Content"
        const val TAG_LABEL = "CalloutMarkdownSpanStyle_Label"
        const val TAG_INDENT = "CalloutMarkdownSpanStyle_Indent"
    }

    private val path = Path()
    private val backgroundColor = Color(51, 51, 51)
    private val cornerRadius = 4.sp
    private val width = 2.sp
    private val horizontalPadding = 12.sp
    private val verticalPadding = 8.sp

    override fun prepare(
        result: TextLayoutResult,
        text: AnnotatedString,
    ) = MarkdownSpanStyle.DrawInstruction {
        val annotations = text.getStringAnnotations(TAG_CONTENT, 0, text.length)

        annotations.fastForEach { annotation ->
            val calloutTypeAnnotation = text.getStringAnnotations(
                tag = TAG_LABEL,
                start = annotation.start,
                end = annotation.end
            ).singleOrNull()?.item

            if (calloutTypeAnnotation != null) {
                drawCalloutStyle(calloutTypeAnnotation, result, annotation, text)
            }

            drawIndentations(text, annotation, result)
        }
    }

    private fun DrawScope.drawCalloutStyle(
        calloutTypeAnnotation: String,
        result: TextLayoutResult,
        annotation: AnnotatedString.Range<String>,
        text: AnnotatedString,
    ) {
        val calloutType = CalloutTypes.getCalloutType(
            context = context,
            calloutType = calloutTypeAnnotation,
            lineHeight = result.getBoundingBox(annotation.start).height.toInt(),
        )

        // Draw the image only when the component is not in focus
        if (text.substring(annotation.start).startsWith("    ")) {
            drawImage(
                calloutType.icon.asImageBitmap(),
                topLeft = Offset(
                    x = result.getHorizontalPosition(annotation.start, true),
                    y = result.getLineTop(result.getLineForOffset(annotation.start)),
                ),
                colorFilter = ColorFilter.tint(calloutType.color),
            )
        }

        val rect = result.getLinesBoundingBox(annotation.start, annotation.end)
        drawRoundRect(
            color = calloutType.color.copy(alpha = 0.1f),
            topLeft = Offset(
                x = rect.left,
                y = rect.top - verticalPadding.toPx(),
            ),
            size = Size(
                width = rect.width,
                height = rect.height + verticalPadding.toPx() * 2,
            ),
            cornerRadius = CornerRadius(cornerRadius.toPx()),
        )
    }

    private fun DrawScope.drawIndentations(
        text: AnnotatedString,
        annotation: AnnotatedString.Range<String>,
        result: TextLayoutResult,
    ) {
        val cornerRadius = CornerRadius(cornerRadius.toPx())

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
