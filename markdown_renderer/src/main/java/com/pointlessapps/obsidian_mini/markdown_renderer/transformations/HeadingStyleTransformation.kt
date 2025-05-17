package com.pointlessapps.obsidian_mini.markdown_renderer.transformations

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

internal data object HeadingStyleTransformation : VisualTransformation {

    private const val TAG = "HEADING"
    private val regex = Regex("(^(#{1,6}) .*?$)", RegexOption.MULTILINE)

    private val spanStyles = listOf(
        SpanStyle(fontWeight = FontWeight.Bold, fontSize = 32.sp),
        SpanStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp),
        SpanStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
        SpanStyle(fontWeight = FontWeight.Bold, fontSize = 21.sp),
        SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
        SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
    )

    private val paragraphStyles = listOf(
        ParagraphStyle(lineHeight = 2.em),
        ParagraphStyle(lineHeight = 1.74.em),
        ParagraphStyle(lineHeight = 1.52.em),
        ParagraphStyle(lineHeight = 1.32.em),
        ParagraphStyle(lineHeight = 1.15.em),
        ParagraphStyle(lineHeight = 1.em),
    )

    override fun filter(text: AnnotatedString): TransformedText {
        val headings = regex.findAll(text.text)

        return TransformedText(
            text = AnnotatedString(
                text = text.text,
                spanStyles = text.spanStyles + headings.map {
                    val headingLevel = it.groupValues[2].length
                    require(headingLevel in 1..6)

                    AnnotatedString.Range(
                        item = spanStyles[headingLevel - 1],
                        start = it.range.first,
                        end = it.range.last + 1,
                        tag = "${TAG}_$headingLevel",
                    )
                }.toList(),
                paragraphStyles = text.paragraphStyles + headings.map {
                    val headingLevel = it.groupValues[2].length
                    require(headingLevel in 1..6)

                    AnnotatedString.Range(
                        item = paragraphStyles[headingLevel - 1],
                        start = it.range.first,
                        end = it.range.last + 1,
                        tag = "${TAG}_$headingLevel",
                    )
                },
            ),
            offsetMapping = OffsetMapping.Identity,
        )
    }
}
