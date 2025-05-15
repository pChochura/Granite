package com.pointlessapps.obsidian_mini.ui_components.utils

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

private inline fun <reified T : Any> Spanned.getSpans(
    start: Int = 0,
    end: Int = length,
): Array<out T> = getSpans(start, end, T::class.java)

internal fun Spanned.toAnnotatedString(
    urlSpanStyle: SpanStyle,
) = buildAnnotatedString {
    append(this@toAnnotatedString.toString())
    val urlSpans = getSpans<URLSpan>()
    val styleSpans = getSpans<StyleSpan>()
    val colorSpans = getSpans<ForegroundColorSpan>()
    val underlineSpans = getSpans<UnderlineSpan>()
    val strikethroughSpans = getSpans<StrikethroughSpan>()
    urlSpans.forEach { urlSpan ->
        val start = getSpanStart(urlSpan)
        val end = getSpanEnd(urlSpan)
        addStyle(urlSpanStyle, start, end)
        addLink(LinkAnnotation.Url(urlSpan.url), start, end)
    }
    colorSpans.forEach { colorSpan ->
        addStyle(
            SpanStyle(color = Color(colorSpan.foregroundColor)),
            getSpanStart(colorSpan),
            getSpanEnd(colorSpan),
        )
    }
    styleSpans.forEach { styleSpan ->
        val start = getSpanStart(styleSpan)
        val end = getSpanEnd(styleSpan)
        when (styleSpan.style) {
            Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
            Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
            Typeface.BOLD_ITALIC -> addStyle(
                SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic),
                start,
                end,
            )
        }
    }
    underlineSpans.forEach { underlineSpan ->
        val start = getSpanStart(underlineSpan)
        val end = getSpanEnd(underlineSpan)
        addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
    }
    strikethroughSpans.forEach { strikethroughSpan ->
        val start = getSpanStart(strikethroughSpan)
        val end = getSpanEnd(strikethroughSpan)
        addStyle(SpanStyle(textDecoration = TextDecoration.LineThrough), start, end)
    }
}
