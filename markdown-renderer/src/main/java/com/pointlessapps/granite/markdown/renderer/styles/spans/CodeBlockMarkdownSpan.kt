package com.pointlessapps.granite.markdown.renderer.styles.spans

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.util.fastForEach
import com.pointlessapps.granite.markdown.renderer.styles.CodeBlockSpanStyle
import com.pointlessapps.granite.markdown.renderer.styles.MarkdownSpanStyle
import com.pointlessapps.granite.markdown.renderer.styles.utils.getLinesBoundingBox

class CodeBlockMarkdownSpan(
    private val style: CodeBlockSpanStyle,
) : MarkdownSpanStyle {

    companion object {
        const val TAG_CONTENT = "CodeBlockMarkdownSpanStyle_Content"
    }

    private val path = Path()

    override fun prepare(
        result: TextLayoutResult,
        text: AnnotatedString,
    ): MarkdownSpanStyle.DrawInstruction {
        path.reset()
        text.getStringAnnotations(0, text.length).fastForEach { annotation ->
            if (!annotation.tag.startsWith(TAG_CONTENT)) return@fastForEach

            val box = result.getLinesBoundingBox(
                startOffset = annotation.start,
                endOffset = annotation.end,
            )
            path.addRoundRect(
                RoundRect(
                    rect = box.inflate(style.backgroundPadding),
                    cornerRadius = CornerRadius(style.backgroundCornerRadius),
                ),
            )
        }

        return MarkdownSpanStyle.DrawInstruction {
            drawPath(path, style.backgroundColor)
        }
    }
}
