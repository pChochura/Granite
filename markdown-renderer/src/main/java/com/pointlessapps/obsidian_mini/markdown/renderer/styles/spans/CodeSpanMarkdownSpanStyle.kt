package com.pointlessapps.obsidian_mini.markdown.renderer.styles.spans

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.MarkdownSpanStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.utils.getBoundingBoxes

object CodeSpanMarkdownSpanStyle : MarkdownSpanStyle {

    const val TAG_CONTENT = "CodeSpanMarkdownSpanStyle_Content"
    const val TAG_DELIMITER = "CodeSpanMarkdownSpanStyle_Delimiter"

    private val path = Path()
    private val backgroundColor = Color(80, 80, 80)
    private val cornerRadius = 4.sp
    private val padding = 2.sp

    private fun getMergedAnnotations(
        annotations: List<AnnotatedString.Range<String>>,
    ): List<IntRange> {
        val mergedAnnotations = mutableListOf<IntRange>()
        var openingDelimiter: IntRange? = null
        var content: IntRange? = null
        annotations.sortedBy { it.start }.fastForEach {
            when (it.item) {
                TAG_DELIMITER -> {
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

        annotations.fastForEach { annotation ->
            val boxes = result.getBoundingBoxes(
                startOffset = annotation.first,
                endOffset = annotation.last,
            )
            boxes.fastForEachIndexed { index, box ->
                path.reset()
                path.addRoundRect(
                    RoundRect(
                        rect = box.inflate(padding.toPx()),
                        topLeft = if (index == 0) cornerRadius else CornerRadius.Zero,
                        bottomLeft = if (index == 0) cornerRadius else CornerRadius.Zero,
                        topRight = if (index == boxes.lastIndex) cornerRadius else CornerRadius.Zero,
                        bottomRight = if (index == boxes.lastIndex) cornerRadius else CornerRadius.Zero,
                    ),
                )
                drawPath(path, backgroundColor)
            }
        }
    }
}
