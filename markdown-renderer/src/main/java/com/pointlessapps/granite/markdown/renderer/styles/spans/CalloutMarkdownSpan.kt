package com.pointlessapps.granite.markdown.renderer.styles.spans

import android.content.Context
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMapNotNull
import com.pointlessapps.granite.markdown.renderer.processors.BlockQuoteProcessor
import com.pointlessapps.granite.markdown.renderer.styles.CalloutSpanStyle
import com.pointlessapps.granite.markdown.renderer.styles.MarkdownSpanStyle
import com.pointlessapps.granite.markdown.renderer.styles.utils.CalloutTypes
import com.pointlessapps.granite.markdown.renderer.styles.utils.getLinesBoundingBox
import com.pointlessapps.granite.markdown.renderer.styles.utils.inflate

class CalloutMarkdownSpan(
    private val style: CalloutSpanStyle,
    private val context: Context,
) : MarkdownSpanStyle {

    private data class Callout(
        val color: Color,
        val icon: Pair<ImageBitmap, Offset>?,
        val background: Rect,
    )

    private val indentationsPath = Path()

    override fun prepare(
        result: TextLayoutResult,
        text: AnnotatedString,
    ): MarkdownSpanStyle.DrawInstruction {
        val annotations = text.getStringAnnotations(BlockQuoteProcessor.TAG, 0, text.length)
        prepareIndentationsPath(result, text, annotations)

        val callouts = annotations.fastMapNotNull { annotation ->
            val calloutTypeAnnotation = text.getStringAnnotations(
                tag = BlockQuoteProcessor.TAG_CALLOUT,
                start = annotation.start,
                end = annotation.end
            ).singleOrNull()?.item ?: return@fastMapNotNull null

            val calloutType = CalloutTypes.getCalloutType(
                context = context,
                calloutType = calloutTypeAnnotation,
                lineHeight = result.getBoundingBox(annotation.start).height.toInt(),
            )

            // Draw the image only when the component is not in focus
            val isInFocus = text.substring(annotation.start).startsWith("\t\t\t")
            Callout(
                color = calloutType.color,
                icon = if (isInFocus) {
                    calloutType.icon.asImageBitmap() to Offset(
                        x = result.getHorizontalPosition(annotation.start, true),
                        y = result.getLineTop(result.getLineForOffset(annotation.start)),
                    )
                } else {
                    null
                },
                background = result.getLinesBoundingBox(annotation.start, annotation.end)
                    .inflate(style.backgroundPadding),
            )
        }

        return MarkdownSpanStyle.DrawInstruction {
            callouts.fastForEach { callout ->
                if (callout.icon != null) {
                    drawImage(
                        image = callout.icon.first,
                        topLeft = callout.icon.second,
                        colorFilter = ColorFilter.tint(callout.color),
                    )
                }

                drawRoundRect(
                    color = callout.color.copy(alpha = 0.1f),
                    topLeft = Offset(
                        x = callout.background.left,
                        y = callout.background.top,
                    ),
                    size = callout.background.size,
                    cornerRadius = CornerRadius(style.cornerRadius),
                )
            }

            drawPath(indentationsPath, style.backgroundColor)
        }
    }

    private fun prepareIndentationsPath(
        result: TextLayoutResult,
        text: AnnotatedString,
        annotations: List<AnnotatedString.Range<String>>,
    ) {
        indentationsPath.reset()
        val cornerRadius = CornerRadius(style.cornerRadius)
        annotations.fastForEach { annotation ->
            val indentRegions = text.getStringAnnotations(
                tag = BlockQuoteProcessor.TAG_INDENT,
                start = annotation.start,
                end = annotation.end,
            )

            indentRegions.fastForEach { indent ->
                val firstLineIndex = result.getLineForOffset(indent.start)
                val lastLineIndex = result.getLineForOffset(indent.end)

                indentationsPath.addRoundRect(
                    RoundRect(
                        rect = Rect(
                            top = result.getLineTop(firstLineIndex) - style.verticalPadding,
                            left = result.getHorizontalPosition(
                                offset = indent.start,
                                usePrimaryDirection = true,
                            ) - style.horizontalPadding,
                            bottom = result.getLineBottom(lastLineIndex) + style.verticalPadding,
                            right = result.getHorizontalPosition(
                                offset = indent.start,
                                usePrimaryDirection = true,
                            ) - style.horizontalPadding + style.width,
                        ),
                        cornerRadius = cornerRadius,
                    ),
                )
            }
        }
    }
}
