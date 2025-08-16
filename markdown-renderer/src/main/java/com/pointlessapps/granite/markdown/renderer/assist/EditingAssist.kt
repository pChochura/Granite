package com.pointlessapps.granite.markdown.renderer.assist

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.util.fastMapNotNull

object EditingAssist {

    val PARENS = mapOf('(' to ')', '[' to ']', '{' to '}')

    const val TAG_LOWER_INDENT = "TAG_LowerIndent"
    const val TAG_HIGHER_INDENT = "TAG_HigherIndent"

    fun process(input: AnnotatedString, selection: TextRange): List<Style> {
        val annotations = input.getStringAnnotations(0, input.length)
        return annotations.sortedBy { it.start }.fastMapNotNull { annotation ->
            if (selection.start >= annotation.start && selection.end <= annotation.end) {
                val range = annotation.start..annotation.end
                return@fastMapNotNull Style(range, annotation.item, annotation.tag)
            }

            null
        }
    }

    fun applyStyle(
        content: TextFieldValue,
        lastActiveStyle: Style?,
        tag: String,
    ): TextFieldValue {
        if (lastActiveStyle == null) {
            val style = Style(
                tag = tag,
                range = IntRange(content.selection.min, content.selection.max),
            )

            return style.applyAt(content)
        }

        val style = Style(
            tag = tag,
            range = lastActiveStyle.range,
            arg = lastActiveStyle.arg.orEmpty(),
        )

        return style.removeFrom(content)
    }
}
