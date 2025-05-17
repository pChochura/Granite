package com.pointlessapps.obsidian_mini.markdown_renderer.transformations

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

internal data object ItalicStyleTransformation : VisualTransformation {

    private const val TAG = "ITALIC"
    private val regex = Regex(
        "((?<!\\*)\\*[^*\\n]+?\\*(?!\\*))|(?:\\s|^)((?<!_)_[^_\\n]+?_(?!_))(?:\\s|$)",
    )

    val style = SpanStyle(fontStyle = FontStyle.Italic)

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
