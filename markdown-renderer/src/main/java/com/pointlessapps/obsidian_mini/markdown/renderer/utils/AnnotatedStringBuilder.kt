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
): AnnotatedString {
    return buildAnnotatedString {
        append(text)
        var parentParagraph: AnnotatedString.Range<ParagraphStyle>? = null
        styles.fastForEach {
            if (it.item !is StringAnnotation && it.tag.isNotEmpty()) {
                addStringAnnotation(it.tag, it.tag, it.start, it.end)
            }

            when (val item = it.item) {
                is SpanStyle -> addStyle(item, it.start, it.end)
                is StringAnnotation -> addStringAnnotation(
                    tag = it.tag,
                    annotation = item.value,
                    start = it.start,
                    end = it.end,
                )

                is ParagraphStyle -> {
                    if (parentParagraph != null && it.start <= parentParagraph.end) {
                        addStyle(item.merge(parentParagraph.item), it.start, it.end)

                        return@fastForEach
                    }

                    parentParagraph = AnnotatedString.Range(item, it.start, it.end)
                    addStyle(item, it.start, it.end)
                }

                else -> {} // TODO cover different cases
            }
        }
    }
}
