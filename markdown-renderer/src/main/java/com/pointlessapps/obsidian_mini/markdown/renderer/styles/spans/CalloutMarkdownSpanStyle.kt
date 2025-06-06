package com.pointlessapps.obsidian_mini.markdown.renderer.styles.spans

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMapNotNull
import com.pointlessapps.obsidian_mini.markdown.renderer.R
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.MarkdownSpanStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.utils.getLinesBoundingBox


class CalloutMarkdownSpanStyle(
    private val context: Context,
) : MarkdownSpanStyle {

    companion object {
        const val TAG_CONTENT = "CalloutMarkdownSpanStyle_Content"
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
        val cornerRadius = CornerRadius(cornerRadius.toPx())
        val annotations = text.getStringAnnotations(0, text.length)
            .fastFilter { it.item == TAG_CONTENT }

        annotations.fastForEach { annotation ->
            val annotationTextContent = text.text.substring(annotation.start, annotation.end)
            val lines = annotationTextContent.lines()
            val linesOffsets = lines.runningFold(0) { length, line -> length + line.length + 1 }
            val indentRegions = getIndentRegions(annotationTextContent)

            val calloutType = "info".toCalloutType(
                lineHeight = result.getBoundingBox(annotation.start).height.toInt(),
            )
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
                cornerRadius = cornerRadius,
            )

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

    private fun String.toCalloutType(lineHeight: Int): CalloutType {
        val icon =
            convertToBitmap(context.getDrawable(R.drawable.ic_note)!!, lineHeight, lineHeight)

        return when (this) {
            "abstract", "summary", "tldr" -> CalloutType(Color(83, 223, 221), icon)
            "info" -> CalloutType(Color(2, 122, 255), icon)
            "todo" -> CalloutType(Color(2, 122, 255), icon)
            "tip", "hint", "important" -> CalloutType(Color(83, 223, 221), icon)
            "success", "check", "done" -> CalloutType(Color(68, 207, 110), icon)
            "question", "help", "faq" -> CalloutType(Color(233, 151, 63), icon)
            "warning", "caution", "attention" -> CalloutType(Color(233, 151, 63), icon)
            "failure", "fail", "missing" -> CalloutType(Color(251, 70, 76), icon)
            "danger", "error" -> CalloutType(Color(251, 70, 76), icon)
            "bug" -> CalloutType(Color(251, 70, 76), icon)
            "example" -> CalloutType(Color(168, 130, 255), icon)
            "quote" -> CalloutType(Color(158, 158, 158), icon)
            else -> CalloutType(Color(2, 122, 255), icon)
        }
    }

    private fun convertToBitmap(drawable: Drawable, widthPixels: Int, heightPixels: Int): Bitmap {
        val bitmap = createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, widthPixels, heightPixels)
        drawable.draw(canvas)
        return bitmap
    }

    private data class CalloutType(
        val color: Color,
        val icon: Bitmap,
    )
}
