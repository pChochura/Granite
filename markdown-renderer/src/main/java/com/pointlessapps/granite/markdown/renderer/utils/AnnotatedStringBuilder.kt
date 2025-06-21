package com.pointlessapps.granite.markdown.renderer.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.StringAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.util.fastForEach

/**
 * Construct a [AnnotatedString] with an addition of merging the nested
 * indentation of the [ParagraphStyle].
 */
internal fun buildAnnotatedString(
    originalText: AnnotatedString,
    text: String,
    styles: List<AnnotatedString.Range<out AnnotatedString.Annotation>>,
) = buildAnnotatedString {
    val originalStyles = mutableListOf<AnnotatedString.Range<out AnnotatedString.Annotation>>()
    originalText.mapAnnotations { it.also(originalStyles::add) }

    append(text)
    val sortedStyles = (originalStyles + styles).sortedBy { it.start }
    var currentIndent: TextIndent
    val paragraphStyleStack = mutableListOf<AnnotatedString.Range<ParagraphStyle>>()
    sortedStyles.fastForEach { style ->
        paragraphStyleStack.removeAll { it.end <= style.start }

        when (val item = style.item) {
            is ParagraphStyle -> {
                var newIndent = item.textIndent ?: TextIndent.Unspecified

                paragraphStyleStack.fastForEach { parentStyle ->
                    parentStyle.item.textIndent?.let { parentIndent ->
                        newIndent = TextIndent(
                            firstLine = newIndent.firstLine + parentIndent.firstLine,
                            restLine = newIndent.restLine + parentIndent.restLine,
                        )
                    }
                }
                currentIndent = newIndent
                addStyle(item.copy(textIndent = currentIndent), style.start, style.end)

                paragraphStyleStack.add(
                    AnnotatedString.Range(
                        item = item,
                        start = style.start,
                        end = style.end,
                        tag = style.tag,
                    ),
                )
                paragraphStyleStack.sortByDescending { it.end }
            }

            is SpanStyle -> addStyle(item, style.start, style.end)
            is StringAnnotation -> addStringAnnotation(
                tag = style.tag,
                annotation = item.value,
                start = style.start,
                end = style.end,
            )

            else -> {} // TODO cover different cases
        }

        if (style.item !is StringAnnotation && style.tag.isNotEmpty()) {
            addStringAnnotation(style.tag, style.tag, style.start, style.end)
        }
    }
}

private val TextIndent.Companion.Unspecified
    get() = TextIndent(TextUnit.Unspecified, TextUnit.Unspecified)

private operator fun TextUnit.plus(other: TextUnit): TextUnit {
    if (this.isUnspecified) return other
    else if (other.isUnspecified) return this

    if (this.isSp && other.isSp || this.isEm && other.isEm) {
        return TextUnit(value + other.value, type)
    }

    throw IllegalArgumentException("Can't add different types of TextUnit")
}
