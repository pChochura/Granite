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
import androidx.compose.ui.util.fastMapNotNull
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.MarkdownSpanStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.utils.CalloutTypes
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.utils.getLinesBoundingBox

class CalloutMarkdownSpanStyle(
    private val context: Context,
) : MarkdownSpanStyle {

    companion object {
        const val TAG_CONTENT = "CalloutMarkdownSpanStyle_Content"
        const val TAG_LABEL = "CalloutMarkdownSpanStyle_Label"
    }

    private val path = Path()
    private val backgroundColor = Color(51, 51, 51)
    private val cornerRadius = 4.sp
    private val width = 2.sp
    private val horizontalPadding = 12.sp
    private val verticalPadding = 8.sp

    private fun getIndents(text: String) = text.lines().fastMapNotNull { line ->
        line.takeWhile { it == '\t' }.count().div(2)
    }

    private fun getIndentRegions(text: String): List<IntRange> {
        val indents = getIndents(text).filter { it > 0 }
        val regions = mutableListOf<IntRange>()
        val stack = mutableListOf<Int>()

        for (i in indents.indices) {
            val currentIndent = indents[i]
            // Close regions if current indent is less than stack top
            while (stack.isNotEmpty() && currentIndent < indents[stack.last()]) {
                val start = stack.removeAt(stack.size - 1)
                regions.add(start..i - 1)
            }
            // Start a new region if indentation increases
            if (stack.isEmpty() || currentIndent > indents[stack.last()]) {
                stack.add(i)
            }
        }

        // Close any remaining regions
        while (stack.isNotEmpty()) {
            val start = stack.removeAt(stack.size - 1)
            regions.add(start..indents.size - 1)
        }

        return regions
    }

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

            drawIndentations(
                annotationTextContent = text.text.substring(annotation.start, annotation.end),
                annotation = annotation,
                result = result,
            )
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
        annotationTextContent: String,
        annotation: AnnotatedString.Range<String>,
        result: TextLayoutResult,
    ) {
        val cornerRadius = CornerRadius(cornerRadius.toPx())
        val indentRegions = getIndentRegions(annotationTextContent)
        val lines = annotationTextContent.lines()
        val linesOffsets = lines.runningFold(0) { length, line -> length + line.length + 1 }
        indentRegions.fastForEach { indent ->
            val padding = if (indent.first == 0) verticalPadding.toPx() else 0f
            val lastTabOffset = annotation.start + linesOffsets[indent.first] +
                    lines[indent.first].takeWhile { it == '\t' }.length
            val firstLineIndex = result.getLineForOffset(
                annotation.start + linesOffsets[indent.first],
            )
            val lastLineIndex = result.getLineForOffset(
                annotation.start + linesOffsets[indent.last],
            )

            path.reset()
            path.addRoundRect(
                RoundRect(
                    rect = Rect(
                        top = result.getLineTop(firstLineIndex) - padding,
                        left = result.getHorizontalPosition(
                            offset = lastTabOffset,
                            usePrimaryDirection = true
                        ) - horizontalPadding.toPx(),
                        bottom = result.getLineBottom(lastLineIndex) + padding,
                        right = result.getHorizontalPosition(
                            offset = lastTabOffset,
                            usePrimaryDirection = true
                        ) - horizontalPadding.toPx() + width.toPx(),
                    ),
                    cornerRadius = cornerRadius,
                ),
            )
            drawPath(path, backgroundColor)
        }
    }
}
