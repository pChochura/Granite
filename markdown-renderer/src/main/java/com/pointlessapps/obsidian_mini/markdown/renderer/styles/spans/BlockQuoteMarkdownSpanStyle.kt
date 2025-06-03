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
import androidx.compose.ui.util.fastMapNotNull
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.MarkdownSpanStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.utils.getLinesBoundingBox

object BlockQuoteMarkdownSpanStyle : MarkdownSpanStyle {

    const val TAG_CONTENT = "BlockQuoteMarkdownSpanStyle_Content"
    const val TAG_DECORATION = "BlockQuoteMarkdownSpanStyle_Delimiter"

    private val path = Path()
    private val backgroundColor = Color(51, 51, 51)
    private val cornerRadius = 4.sp
    private val width = 4.sp

    private fun getMergedAnnotations(
        annotations: List<AnnotatedString.Range<String>>,
    ): MutableList<IntRange> {
        val mergedAnnotations = mutableListOf<IntRange>()
        var openingDelimiter: IntRange? = null
        var content: IntRange? = null
        annotations.sortedBy { it.start }.fastForEach {
            when (it.item) {
                TAG_DECORATION -> {
                    if (openingDelimiter == null) {
                        openingDelimiter = it.start..it.end
                    } else if (content != null) {
                        mergedAnnotations.add(openingDelimiter!!.first..it.end)
                        openingDelimiter = null
                        content = null
                    }
                }

                TAG_CONTENT -> {
                    if (openingDelimiter != null) {
                        content = it.start..it.end
                    } else {
                        mergedAnnotations.add(it.start..it.end)
                    }
                }
            }
        }

        return mergedAnnotations
    }

    override fun prepare(
        result: TextLayoutResult,
        text: AnnotatedString,
    ) = MarkdownSpanStyle.DrawInstruction {
        val cornerRadius = CornerRadius(cornerRadius.toPx())
        val annotations = getMergedAnnotations(text.getStringAnnotations(0, text.length))

        annotations.fastMapNotNull { annotation ->
            val box = result.getLinesBoundingBox(
                startOffset = annotation.first,
                endOffset = annotation.last,
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
