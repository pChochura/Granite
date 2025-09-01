package com.pointlessapps.granite.markdown.renderer.assist

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.pointlessapps.granite.markdown.renderer.assist.EditingAssist.TAG_HIGHER_INDENT
import com.pointlessapps.granite.markdown.renderer.assist.EditingAssist.TAG_LOWER_INDENT
import com.pointlessapps.granite.markdown.renderer.assist.utils.offset
import com.pointlessapps.granite.markdown.renderer.processors.BlockQuoteProcessor
import com.pointlessapps.granite.markdown.renderer.processors.BoldProcessor
import com.pointlessapps.granite.markdown.renderer.processors.CodeBlockProcessor
import com.pointlessapps.granite.markdown.renderer.processors.CodeSpanProcessor
import com.pointlessapps.granite.markdown.renderer.processors.CommentProcessor
import com.pointlessapps.granite.markdown.renderer.processors.FootnoteLinkProcessor
import com.pointlessapps.granite.markdown.renderer.processors.HashtagProcessor
import com.pointlessapps.granite.markdown.renderer.processors.HeaderProcessor
import com.pointlessapps.granite.markdown.renderer.processors.HighlightProcessor
import com.pointlessapps.granite.markdown.renderer.processors.ImageProcessor
import com.pointlessapps.granite.markdown.renderer.processors.InlineLinkProcessor
import com.pointlessapps.granite.markdown.renderer.processors.InternalLinkProcessor
import com.pointlessapps.granite.markdown.renderer.processors.ItalicProcessor
import com.pointlessapps.granite.markdown.renderer.processors.OrderedListProcessor
import com.pointlessapps.granite.markdown.renderer.processors.StrikethroughProcessor
import com.pointlessapps.granite.markdown.renderer.processors.UnorderedListProcessor

data class Style(val range: IntRange, val tag: String, val arg: String? = null) {
    fun applyAt(text: TextFieldValue) = when (tag) {
        HeaderProcessor.TAG -> applyAtEveryLine(text) { "${"#".repeat(arg?.toIntOrNull() ?: 1)} " }
        BoldProcessor.TAG -> applyAtDelimited(text, "**")
        ItalicProcessor.TAG -> applyAtDelimited(text, "*")
        StrikethroughProcessor.TAG -> applyAtDelimited(text, "~~")
        HighlightProcessor.TAG -> applyAtDelimited(text, "==")
        CommentProcessor.TAG -> applyAtDelimited(text, "%%")
        CodeSpanProcessor.TAG -> applyAtDelimited(text, "`")
        OrderedListProcessor.TAG -> applyAtEveryLine(text) { "${it + 1}. " }
        UnorderedListProcessor.TAG -> applyAtEveryLine(text) { "- " }
        BlockQuoteProcessor.TAG -> applyAtEveryLine(text) { "> " }
        BlockQuoteProcessor.TAG_CALLOUT -> applyAtEveryLine(text) { if (it == 0) "> [!info] \n> " else "> " }
        CodeBlockProcessor.TAG -> applyBlockAt(text, "```")
        InternalLinkProcessor.TAG -> applyAtDelimited(text, "[[", "]]")
        FootnoteLinkProcessor.TAG -> applyAtDelimited(text, "[^", "]")
        ImageProcessor.TAG -> applyAtDelimited(text, "![](", ")")
        HashtagProcessor.TAG -> applyAtDelimited(text, "#", "")
        TAG_LOWER_INDENT -> removeFromEveryLine(text, Regex("^ {2}"))
        TAG_HIGHER_INDENT -> applyAtEveryLine(text) { "  " }
        else -> throw IllegalArgumentException("Unknown style tag: $tag")
    }

    fun removeFrom(text: TextFieldValue) = when (tag) {
        HeaderProcessor.TAG -> removeFromEveryLine(text, Regex("^#{1,6} "))
        BoldProcessor.TAG -> removeDelimited(text, Regex("^\\*\\*"), Regex("\\*\\*$"))
        ItalicProcessor.TAG -> removeDelimited(text, Regex("^\\*"), Regex("\\*$"))
        StrikethroughProcessor.TAG -> removeDelimited(text, Regex("^~~"), Regex("~~$"))
        HighlightProcessor.TAG -> removeDelimited(text, Regex("^=="), Regex("==$"))
        CommentProcessor.TAG -> removeDelimited(text, Regex("^%%"), Regex("%%$"))
        CodeSpanProcessor.TAG -> removeDelimited(text, Regex("^`"), Regex("`$"))
        OrderedListProcessor.TAG -> removeFromEveryLine(text, Regex("^\\d+\\. "))
        UnorderedListProcessor.TAG -> removeFromEveryLine(text, Regex("^- "))
        BlockQuoteProcessor.TAG -> removeFromEveryLine(text, Regex("^> "))
        BlockQuoteProcessor.TAG_CALLOUT -> removeFromEveryLine(text, Regex("^> "))
        CodeBlockProcessor.TAG -> removeBlock(text, Regex("^```.*"), Regex("```"))
        InternalLinkProcessor.TAG -> removeDelimited(text, Regex("^\\[\\["), Regex("]]$"))
        InlineLinkProcessor.TAG -> removeDelimited(text, Regex("^\\[.*?]\\("), Regex("\\)$"))
        FootnoteLinkProcessor.TAG -> removeDelimited(text, Regex("^\\[\\^"), Regex("]$"))
        ImageProcessor.TAG -> removeDelimited(text, Regex("^!\\[.*?]\\("), Regex("\\)$"))
        HashtagProcessor.TAG -> removeDelimited(text, Regex("^#"), Regex(""))
        else -> throw IllegalArgumentException("Unknown style tag: $tag")
    }

    private fun applyAtEveryLine(
        text: TextFieldValue,
        delimiter: (line: Int) -> String,
    ): TextFieldValue {
        val inputText = text.text
        val lineStart = inputText.substring(0, range.min()).lastIndexOf('\n')
            .takeIf { it != -1 }?.plus(1) ?: 0
        val lines = listOf(lineStart) + inputText.substring(range.min(), range.max())
            .mapIndexedNotNull { index, c -> if (c != '\n') null else index + range.min() + 1 }

        var addedSize = 0
        return text.copy(
            text = buildString {
                append(inputText)
                // Insert in a backwards order so that indices are still valid
                lines.asReversed().forEachIndexed { line, charIndex ->
                    insert(
                        charIndex,
                        delimiter(lines.size - line - 1).also {
                            addedSize += it.length
                        },
                    )
                }
            },
            selection = TextRange(
                start = text.selection.start + delimiter(0).length,
                end = text.selection.end + addedSize,
            ),
        )
    }

    private fun removeFromEveryLine(text: TextFieldValue, regex: Regex): TextFieldValue {
        val inputText = text.text
        val lineStart = inputText.substring(0, range.min()).lastIndexOf('\n')
            .takeIf { it != -1 }?.plus(1) ?: 0
        val lines = listOf(lineStart) + inputText.substring(range.min(), range.max())
            .mapIndexedNotNull { index, c -> if (c != '\n') null else index + range.min() + 1 }

        var removedSize = 0
        return text.copy(
            text = buildString {
                append(inputText)
                // Insert in a backwards order so that indices are still valid
                lines.asReversed().forEachIndexed { line, charIndex ->
                    regex.find(inputText.substring(charIndex))?.let {
                        delete(charIndex, charIndex + it.range.max() + 1)
                        if (charIndex <= text.selection.start) {
                            removedSize += it.range.max() + 1
                        }
                    }
                }
            },
            selection = text.selection.offset(-removedSize),
        )
    }

    private fun applyAtDelimited(
        text: TextFieldValue,
        prefix: String,
        suffix: String = prefix,
    ): TextFieldValue {
        val inputText = text.text

        return text.copy(
            text = buildString {
                append(inputText)
                // Insert in a backwards order so that indices are still valid
                if (suffix.isNotEmpty()) insert(range.max(), suffix)
                if (prefix.isNotEmpty()) insert(range.min(), prefix)
            },
            selection = text.selection.offset(prefix.length),
        )
    }

    private fun removeDelimited(
        text: TextFieldValue,
        prefix: Regex,
        suffix: Regex,
    ): TextFieldValue {
        val inputText = text.text
        var prefixSize = 0
        return text.copy(
            text = buildString {
                append(inputText)
                if (suffix.pattern.isNotEmpty()) {
                    suffix.find(inputText.substring(range.min(), range.max()))?.let {
                        delete(range.min() + it.range.min(), range.min() + it.range.max() + 1)
                    }
                }

                if (prefix.pattern.isNotEmpty()) {
                    prefix.find(inputText.substring(range.min()))?.let {
                        delete(range.min(), range.min() + it.range.max() + 1)
                        prefixSize += it.range.max() + 1
                    }
                }
            },
            selection = text.selection.offset(-prefixSize),
        )
    }

    private fun applyBlockAt(
        text: TextFieldValue,
        prefix: String,
        suffix: String = prefix,
    ): TextFieldValue {
        val inputText = text.text
        val lineStart = inputText.substring(0, range.min()).lastIndexOf('\n')
        val lineEnd = inputText.indexOf('\n', range.max())
            .takeIf { it != -1 } ?: range.max()

        return text.copy(
            text = buildString {
                append(inputText)
                // Insert in a backwards order so that indices are still valid
                if (suffix.isNotEmpty()) insert(lineEnd, "\n" + suffix)
                if (prefix.isNotEmpty()) insert(lineStart + 1, prefix + "\n")
            },
            selection = text.selection.offset(if (prefix.isNotEmpty()) prefix.length + 1 else 0),
        )
    }

    private fun removeBlock(
        text: TextFieldValue,
        prefix: Regex,
        suffix: Regex = prefix,
    ): TextFieldValue {
        val inputText = text.text
        val prefixLength = prefix.find(inputText.substring(range.min()))?.let {
            it.range.max() - it.range.min() + 1
        } ?: 0

        val firstLineEnd = inputText.indexOf('\n', range.min())
            .takeIf { it != -1 } ?: (range.min() + prefixLength)
        val lastLineStart = inputText.substring(0, range.max()).lastIndexOf('\n')
            .takeIf { it != -1 } ?: range.max()

        val suffixRange = suffix.find(
            inputText.substring(lastLineStart, range.max()),
        )?.range ?: IntRange.EMPTY

        return text.copy(
            text = buildString {
                append(inputText.substring(0, range.min()))
                append(inputText.substring(firstLineEnd + 1, lastLineStart))
                append(inputText.substring(lastLineStart + 1, suffixRange.min() + lastLineStart))
                append(inputText.substring(lastLineStart + suffixRange.max() + 1))
            },
            selection = text.selection.offset(-prefixLength - 1),
        )
    }
}
