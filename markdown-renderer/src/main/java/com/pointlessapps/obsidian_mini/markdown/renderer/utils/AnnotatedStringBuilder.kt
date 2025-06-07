package com.pointlessapps.obsidian_mini.markdown.renderer.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.StringAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.util.fastForEach

internal fun buildAnnotatedString(
    text: String,
    styles: List<AnnotatedString.Range<AnnotatedString.Annotation>>,
) = buildAnnotatedString {
    append(text)
    styles.fastForEach { style ->
        if (style.item !is StringAnnotation && style.tag.isNotEmpty()) {
            addStringAnnotation(style.tag, style.tag, style.start, style.end)
        }

        when (val item = style.item) {
            is ParagraphStyle -> addStyle(item, style.start, style.end)
            is SpanStyle -> addStyle(item, style.start, style.end)
            is StringAnnotation -> addStringAnnotation(
                tag = style.tag,
                annotation = item.value,
                start = style.start,
                end = style.end,
            )

            else -> {} // TODO cover different cases
        }
    }
}
