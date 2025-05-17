package com.pointlessapps.obsidian_mini.markdown_renderer.transformations

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

internal data object InlineCodeBlockStyleTransformation : VisualTransformation {

    private const val TAG = "INLINE_CODE"
    private val regex = Regex("`([^\n`]+?)`")

    private val style = SpanStyle(
        background = Color.DarkGray.copy(alpha = 0.4f),
        fontFamily = FontFamily.Monospace
    )

    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        val transformedBuilder = StringBuilder()
        val originalToTransformedMap = IntArray(original.length + 1) { -1 }
        val transformedToOriginalMap = mutableListOf<Int>()
        val spanStyles = mutableListOf<AnnotatedString.Range<SpanStyle>>()

        var currentIndex = 0

        for (match in regex.findAll(original)) {
            val start = match.range.first
            val end = match.range.last
            val codeContentStart = start + 1
            val codeContentEnd = end // exclusive

            // Add text before match
            for (i in currentIndex until start) {
                originalToTransformedMap[i] = transformedBuilder.length
                transformedBuilder.append(original[i])
                transformedToOriginalMap.add(i)
            }

            // Add code content without backticks
            val styleStart = transformedBuilder.length
            for (i in codeContentStart until codeContentEnd) {
                originalToTransformedMap[i] = transformedBuilder.length
                transformedBuilder.append(original[i])
                transformedToOriginalMap.add(i)
            }
            val styleEnd = transformedBuilder.length

            spanStyles.add(AnnotatedString.Range(style, styleStart, styleEnd, TAG))

            // Mark backticks as removed
            originalToTransformedMap[start] = -1
            originalToTransformedMap[end] = -1

            currentIndex = end + 1
        }

        // Add remaining text
        for (i in currentIndex until original.length) {
            originalToTransformedMap[i] = transformedBuilder.length
            transformedBuilder.append(original[i])
            transformedToOriginalMap.add(i)
        }

        // Trailing offset mapping
        originalToTransformedMap[original.length] = transformedBuilder.length
        transformedToOriginalMap.add(original.length)

        return TransformedText(
            AnnotatedString(
                text = transformedBuilder.toString(),
                spanStyles = text.spanStyles + spanStyles,
                paragraphStyles = text.paragraphStyles,
            ),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    if (offset <= 0) return 0
                    if (offset >= originalToTransformedMap.size) return transformedBuilder.length
                    for (i in offset downTo 0) {
                        val mapped = originalToTransformedMap[i]
                        if (mapped != -1) return mapped + (offset - i)
                    }
                    return 0
                }

                override fun transformedToOriginal(offset: Int): Int {
                    if (offset <= 0) return 0
                    if (offset >= transformedToOriginalMap.size) return original.length
                    return transformedToOriginalMap[offset]
                }
            }
        )
    }
}

