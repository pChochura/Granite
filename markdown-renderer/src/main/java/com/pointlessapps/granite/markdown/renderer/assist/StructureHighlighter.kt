package com.pointlessapps.granite.markdown.renderer.assist

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

internal class StructureHighlighter {

    private companion object {
        val supportedBrackets = mapOf(
            '(' to ')',
            '[' to ']',
            '{' to '}',
        ).flatMap { listOf(it.key to it.value, it.value to it.key) }.toMap()
    }

    private fun styleBracketAt(index: Int) = AnnotatedString.Range(
        item = SpanStyle(
            fontWeight = FontWeight.Companion.SemiBold,
            textDecoration = TextDecoration.Companion.Underline,
        ),
        start = index,
        end = index + 1,
    )

    private fun processAtPosition(
        text: String,
        position: Int,
    ): List<AnnotatedString.Range<out AnnotatedString.Annotation>>? {
        val startingBracket = text.getOrNull(position) ?: return null
        val matchingBracket = supportedBrackets[startingBracket] ?: return null

        val range = if (startingBracket < matchingBracket) {
            (position + 1)..text.lastIndex
        } else {
            (position - 1) downTo 0
        }

        var bracketsToSkip = 0
        for (index in range) {
            when (text[index]) {
                startingBracket -> bracketsToSkip++
                matchingBracket -> {
                    if (bracketsToSkip == 0) {
                        return listOf(
                            styleBracketAt(position),
                            styleBracketAt(index),
                        )
                    }

                    bracketsToSkip--
                }
            }
        }

        return null
    }

    // TODO save the positions of the brackets and compute that only when the [text] changes
    fun processCursorPosition(
        text: String,
        cursorPosition: TextRange,
    ): List<AnnotatedString.Range<out AnnotatedString.Annotation>> {
        if (!cursorPosition.collapsed) {
            return emptyList()
        }

        // First match a case where the cursor is right before a bracket
        // and then try when it is right after one
        return processAtPosition(
            text = text, position = cursorPosition.start,
        ) ?: processAtPosition(
            text = text, position = cursorPosition.start - 1,
        ) ?: emptyList()
    }
}
