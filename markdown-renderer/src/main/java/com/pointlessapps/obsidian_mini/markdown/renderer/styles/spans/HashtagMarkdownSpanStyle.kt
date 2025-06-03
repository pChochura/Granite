package com.pointlessapps.obsidian_mini.markdown.renderer.styles.spans

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.MarkdownSpanStyle
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.utils.getBoundingBoxes
import com.pointlessapps.obsidian_mini.markdown.renderer.styles.utils.inflate

object HashtagMarkdownSpanStyle : MarkdownSpanStyle {

    const val TAG_CONTENT = "HashtagMarkdownSpanStyle_Content"

    private val path = Path()
    private val backgroundColor = Color(76, 175, 80)
    private val cornerRadius = 16.sp
    private val horizontalPadding = 4.sp
    private val verticalPadding = 2.sp

    override fun prepare(
        result: TextLayoutResult,
        text: AnnotatedString,
    ) = MarkdownSpanStyle.DrawInstruction {
        val cornerRadius = CornerRadius(cornerRadius.toPx())
        val annotations = text.getStringAnnotations(0, text.length)
            .fastFilter { it.item == TAG_CONTENT }

        annotations.fastForEach { annotation ->
            val boxes = result.getBoundingBoxes(
                startOffset = annotation.start,
                endOffset = annotation.end,
            )
            boxes.fastForEachIndexed { index, box ->
                path.reset()
                path.addRoundRect(
                    RoundRect(
                        rect = box.inflate(verticalPadding.toPx(), horizontalPadding.toPx()),
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
