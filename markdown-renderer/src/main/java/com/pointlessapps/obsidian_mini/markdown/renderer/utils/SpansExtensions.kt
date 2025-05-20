package com.pointlessapps.obsidian_mini.markdown.renderer.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.util.fastForEach

fun AnnotatedString.copy(builder: AnnotatedString.Builder.() -> Unit): AnnotatedString = buildAnnotatedString {
    append(text)
    spanStyles.fastForEach { addStyle(it.item, it.start, it.end) }
    paragraphStyles.fastForEach { addStyle(it.item, it.start, it.end) }
    getStringAnnotations(start = 0, end = text.length).fastForEach {
        addStringAnnotation(
            tag = it.tag,
            annotation = it.item,
            start = it.start,
            end = it.end,
        )
    }
    getTtsAnnotations(start = 0, end = text.length).fastForEach {
        addTtsAnnotation(it.item, it.start, it.end)
    }
    builder()
}
