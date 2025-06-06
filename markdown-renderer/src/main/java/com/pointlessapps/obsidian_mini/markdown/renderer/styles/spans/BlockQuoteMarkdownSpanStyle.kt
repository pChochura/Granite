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
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMapNotNull
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.MarkdownSpanStyle

object BlockQuoteMarkdownSpanStyle : MarkdownSpanStyle {

    const val TAG_CONTENT = "BlockQuoteMarkdownSpanStyle_Content"

    private val path = Path()
    private val backgroundColor = Color(51, 51, 51)
    private val cornerRadius = 4.sp
    private val width = 2.sp
    private val horizontalPadding = 12.sp
    private val verticalPadding = 4.sp

    private val markerRegex = Regex("^(\t\t)*")

    private fun getIndents(text: String) = text.lines().fastMapNotNull { line ->
        val matchedGroup = markerRegex.find(line)?.groups?.firstOrNull()

        // Make sure it starts at 0
        return@fastMapNotNull matchedGroup?.value?.count { it == '\t' }?.div(2)
    }

    private fun getIndentRegions(text: String): List<IntRange> {
        val indents = getIndents(text)
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
        val lines = text.lines()
        val linesOffsets = lines.runningFold(0) { length, line -> length + line.length + 1 }
        val cornerRadius = CornerRadius(cornerRadius.toPx())
        val annotations = text.getStringAnnotations(0, text.length)
            .fastFilter { it.item == TAG_CONTENT }

        annotations.fastForEach { annotation ->
            val indentRegions = getIndentRegions(
                text.text.substring(annotation.start, annotation.end),
            ).sortedBy { it.start }

            indentRegions.fastForEach { indent ->
                val padding = if (indent.first == 0) verticalPadding.toPx() else 0f
                val lastTabOffset = linesOffsets[indent.first] +
                        lines[indent.first].takeWhile { it == '\t' }.length
                path.reset()
                path.addRoundRect(
                    RoundRect(
                        rect = Rect(
                            top = result.getLineTop(indent.first) - padding,
                            left = result.getHorizontalPosition(
                                offset = lastTabOffset,
                                usePrimaryDirection = true
                            ) - horizontalPadding.toPx(),
                            bottom = result.getLineBottom(indent.last) + padding,
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
}
