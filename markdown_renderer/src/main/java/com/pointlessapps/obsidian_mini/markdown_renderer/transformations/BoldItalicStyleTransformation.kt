package com.pointlessapps.obsidian_mini.markdown_renderer.transformations

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

internal data object BoldItalicStyleTransformation : VisualTransformation {

    private const val TAG = "BOLD_ITALIC"
    private val regex = Regex(
        "((?<!\\*)\\*\\*\\*[^*\\n]+?\\*\\*\\*(?!\\*))|(?:\\s|^)((?<!_)___[^_\\n]+?___(?!_))(?:\\s|$)",
    )

    private val style = BoldStyleTransformation.style + ItalicStyleTransformation.style

    override fun filter(text: AnnotatedString) = TransformedText(
        text = AnnotatedString(
            text = text.text,
            spanStyles = text.spanStyles + regex.findAll(text.text).map {
                AnnotatedString.Range(style, it.range.first, it.range.last + 1, TAG)
            }.toList(),
            paragraphStyles = text.paragraphStyles,
        ),
        offsetMapping = OffsetMapping.Identity,
    )
}
