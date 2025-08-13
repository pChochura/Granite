package com.pointlessapps.granite.markdown.renderer.styles.spans

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.util.fastForEach
import com.pointlessapps.granite.markdown.renderer.processors.BlockQuoteProcessor
import com.pointlessapps.granite.markdown.renderer.styles.BlockQuoteSpanStyle
import com.pointlessapps.granite.markdown.renderer.styles.MarkdownSpanStyle

class BlockQuoteMarkdownSpan(
    private val style: BlockQuoteSpanStyle,
) : MarkdownSpanStyle {

    private val path = Path()

    override fun prepare(
        result: TextLayoutResult,
        text: AnnotatedString,
    ): MarkdownSpanStyle.DrawInstruction {
        val cornerRadius = CornerRadius(style.cornerRadius)
        val annotations = text.getStringAnnotations(BlockQuoteProcessor.TAG, 0, text.length)

        path.reset()
        annotations.fastForEach { annotation ->
            val indentRegions = text.getStringAnnotations(
                tag = BlockQuoteProcessor.TAG_INDENT,
                start = annotation.start,
                end = annotation.end,
            )

            indentRegions.fastForEach { indent ->
                val firstLineIndex = result.getLineForOffset(indent.start)
                val lastLineIndex = result.getLineForOffset(indent.end)

                path.addRoundRect(
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

        return MarkdownSpanStyle.DrawInstruction {
            drawPath(path, style.backgroundColor)
        }
    }
}
