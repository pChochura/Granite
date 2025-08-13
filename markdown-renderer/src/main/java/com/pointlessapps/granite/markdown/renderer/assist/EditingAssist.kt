package com.pointlessapps.granite.markdown.renderer.assist

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.util.fastMapNotNull
import com.pointlessapps.granite.markdown.renderer.processors.BlockIdProcessor
import com.pointlessapps.granite.markdown.renderer.processors.BlockQuoteProcessor
import com.pointlessapps.granite.markdown.renderer.processors.BoldProcessor
import com.pointlessapps.granite.markdown.renderer.processors.CodeBlockProcessor
import com.pointlessapps.granite.markdown.renderer.processors.CodeSpanProcessor
import com.pointlessapps.granite.markdown.renderer.processors.CommentBlockProcessor
import com.pointlessapps.granite.markdown.renderer.processors.CommentProcessor
import com.pointlessapps.granite.markdown.renderer.processors.EmbedProcessor
import com.pointlessapps.granite.markdown.renderer.processors.FootnoteDefinitionProcessor
import com.pointlessapps.granite.markdown.renderer.processors.FootnoteLinkProcessor
import com.pointlessapps.granite.markdown.renderer.processors.HashtagProcessor
import com.pointlessapps.granite.markdown.renderer.processors.HeaderProcessor
import com.pointlessapps.granite.markdown.renderer.processors.HighlightProcessor
import com.pointlessapps.granite.markdown.renderer.processors.HorizontalRuleProcessor
import com.pointlessapps.granite.markdown.renderer.processors.ImageProcessor
import com.pointlessapps.granite.markdown.renderer.processors.InlineFootnoteProcessor
import com.pointlessapps.granite.markdown.renderer.processors.InlineLinkProcessor
import com.pointlessapps.granite.markdown.renderer.processors.InternalLinkProcessor
import com.pointlessapps.granite.markdown.renderer.processors.ItalicProcessor
import com.pointlessapps.granite.markdown.renderer.processors.OrderedListProcessor
import com.pointlessapps.granite.markdown.renderer.processors.StrikethroughProcessor
import com.pointlessapps.granite.markdown.renderer.processors.UnorderedListProcessor

object EditingAssist {
    private fun getStyleFromTag(tag: String, range: IntRange) = when (tag) {
        HeaderProcessor.TAG -> Style.Heading(1, range)
        BoldProcessor.TAG -> Style.Bold(range)
        ItalicProcessor.TAG -> Style.Italic(range)
        StrikethroughProcessor.TAG -> Style.Strikethrough(range)
        HighlightProcessor.TAG -> Style.Highlight(range)
        CommentProcessor.TAG -> Style.Comment(range)
        OrderedListProcessor.TAG -> Style.OrderedList(range)
        UnorderedListProcessor.TAG -> Style.UnorderedList(range)
        BlockQuoteProcessor.TAG -> Style.BlockQuote(range)
        BlockQuoteProcessor.TAG_CALLOUT -> Style.Callout("info", range)
        CodeBlockProcessor.TAG -> Style.CodeBlock(range)
        CommentBlockProcessor.TAG -> Style.CommentBlock(range)
        CodeSpanProcessor.TAG -> Style.CodeSpan(range)
        InternalLinkProcessor.TAG -> Style.InternalLink(range)
        InlineLinkProcessor.TAG -> Style.InlineLink(range)
        FootnoteLinkProcessor.TAG -> Style.FootnoteLink(range)
        ImageProcessor.TAG -> Style.Image(range)
        EmbedProcessor.TAG -> Style.Embed(range)
        BlockIdProcessor.TAG -> Style.BlockId(range)
        FootnoteDefinitionProcessor.TAG -> Style.FootnoteDefinition(range)
        InlineFootnoteProcessor.TAG -> Style.InlineFootnote(range)
        HashtagProcessor.TAG -> Style.Hashtag(range)
        HorizontalRuleProcessor.TAG -> Style.HorizontalRule(range)
        else -> null
    }

    fun process(input: AnnotatedString, selection: TextRange): List<Style> {
        val annotations = input.getStringAnnotations(start = 0, end = input.length)
        return annotations.sortedBy { it.start }.fastMapNotNull { annotation ->
            if (selection.start >= annotation.start && selection.end <= annotation.end) {
                val range = annotation.start..annotation.end
                return@fastMapNotNull getStyleFromTag(annotation.tag, range)
//                HeaderProcessor.TAG -> Style.Heading(annotation.item.toInt(), range)
//                BlockQuoteProcessor.TAG_CALLOUT -> Style.Callout(annotation.item, range)
            }

            null
        }
    }

    fun applyStyle(
        content: TextFieldValue,
        tag: String,
    ): TextFieldValue {
        val style = getStyleFromTag(tag, IntRange(content.selection.start, content.selection.end))
        return style?.applyAt(content) ?: content
    }
}
