package com.pointlessapps.granite.markdown.renderer.assist

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

sealed class Style(val range: IntRange) {
    abstract fun applyAt(text: TextFieldValue): TextFieldValue

    class Heading(val level: Int, range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.insertAtTheLineStart(range, "${"#".repeat(level)} ")
    }

    class Bold(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.surroundWithAt(range, "**")
    }

    class Italic(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.surroundWithAt(range, "*")
    }

    class Strikethrough(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.surroundWithAt(range, "~~")
    }

    class Highlight(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.surroundWithAt(range, "==")
    }

    class Comment(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.surroundWithAt(range, "%%")
    }

    class CodeSpan(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.surroundWithAt(range, "`")
    }

    class OrderedList(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.insertAtTheLineStart(range, "1. ")
    }

    class UnorderedList(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.insertAtTheLineStart(range, "- ")
    }

    class BlockQuote(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.surroundWithAt(range, "\n> ", "\n")
    }

    class Callout(val type: String, range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.surroundWithAt(range, "\n> [!$type] \n> ", "\n")
    }

    class CodeBlock(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.surroundWithAt(range, "\n```\n", "\n```\n")
    }

    class CommentBlock(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.surroundWithAt(range, "\n%%\n", "\n%%\n")
    }

    class InternalLink(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.surroundWithAt(range, "[[", "]]")
    }

    class InlineLink(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.surroundWithAt(range, "[](", ")")
    }

    class FootnoteLink(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.surroundWithAt(range, "[^", "]")
    }

    class Image(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.surroundWithAt(range, "![](", ")")
    }

    class Embed(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.surroundWithAt(range, "![[", "]]")
    }

    class BlockId(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.surroundWithAt(range, "^", "")
    }

    class FootnoteDefinition(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.surroundWithAt(range, "[^", "]:")
    }

    class InlineFootnote(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.surroundWithAt(range, "^[", "]")
    }

    class Hashtag(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.surroundWithAt(range, "#", "")
    }

    class HorizontalRule(range: IntRange) : Style(range) {
        override fun applyAt(text: TextFieldValue) =
            text.surroundWithAt(range, "---", "")
    }
}

private fun TextFieldValue.surroundWithAt(
    range: IntRange,
    prefix: String,
    suffix: String = prefix,
) = copy(
    text = buildString {
        append(text.substring(0, range.first))
        append(prefix)
        append(text.substring(range.first, range.last))
        append(suffix)
        append(text.substring(range.last, text.length))
    },
    selection = TextRange(
        start = range.first + prefix.length,
        end = range.last + prefix.length,
    ),
)

private fun TextFieldValue.insertAtTheLineStart(
    range: IntRange,
    prefix: String,
) = copy(
    text = buildString {
        var startingIndex = range.start
        while (startingIndex > 0 && text.getOrNull(startingIndex - 1) != '\n') {
            startingIndex--
        }
        append(text.substring(0, startingIndex))
        append(prefix)
        append(text.substring(startingIndex, text.length))
    },
    selection = TextRange(
        start = range.first + prefix.length,
        end = range.last + prefix.length,
    ),
)
