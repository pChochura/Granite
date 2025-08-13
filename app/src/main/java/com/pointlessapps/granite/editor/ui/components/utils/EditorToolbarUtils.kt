package com.pointlessapps.granite.editor.ui.components.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pointlessapps.granite.R
import com.pointlessapps.granite.editor.ui.components.EditorToolbarItem
import com.pointlessapps.granite.markdown.renderer.assist.Style
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
import com.pointlessapps.granite.markdown.renderer.processors.InternalLinkProcessor
import com.pointlessapps.granite.markdown.renderer.processors.ItalicProcessor
import com.pointlessapps.granite.markdown.renderer.processors.OrderedListProcessor
import com.pointlessapps.granite.markdown.renderer.processors.StrikethroughProcessor
import com.pointlessapps.granite.markdown.renderer.processors.UnorderedListProcessor

@Composable
internal fun getEditorToolbarItems(activeStyles: List<Style>) = listOf(
    EditorToolbarItem.Extended(
        iconRes = R.drawable.icon_text_size,
        name = stringResource(
            id = R.string.heading_n,
            activeStyles.filterIsInstance<Style.Heading>().maxOfOrNull { it.level } ?: 1,
        ),
        active = activeStyles.any { it is Style.Heading },
        tag = HeaderProcessor.TAG,
    ),
    EditorToolbarItem.Separator,
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_bold,
        tooltip = stringResource(R.string.bold),
        active = activeStyles.any { it is Style.Bold },
        tag = BoldProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_italic,
        tooltip = stringResource(R.string.italic),
        active = activeStyles.any { it is Style.Italic },
        tag = ItalicProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_strikethrough,
        tooltip = stringResource(R.string.strikethrough),
        active = activeStyles.any { it is Style.Strikethrough },
        tag = StrikethroughProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_highlight,
        tooltip = stringResource(R.string.highlight),
        active = activeStyles.any { it is Style.Highlight },
        tag = HighlightProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_code_span,
        tooltip = stringResource(R.string.code_span),
        active = activeStyles.any { it is Style.CodeSpan },
        tag = CodeSpanProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_comment,
        tooltip = stringResource(R.string.comment),
        active = activeStyles.any { it is Style.Comment },
        tag = CommentProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_tag,
        tooltip = stringResource(R.string.tag),
        active = activeStyles.any { it is Style.Hashtag },
        tag = HashtagProcessor.TAG,
    ),
    EditorToolbarItem.Separator,
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_ordered_list,
        tooltip = stringResource(R.string.ordered_list),
        active = activeStyles.any { it is Style.OrderedList },
        tag = OrderedListProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_unordered_list,
        tooltip = stringResource(R.string.unordered_list),
        active = activeStyles.any { it is Style.UnorderedList },
        tag = UnorderedListProcessor.TAG,
    ),
    EditorToolbarItem.Separator,
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_indent_lower,
        tooltip = stringResource(R.string.lower_indent),
        active = false,
        tag = "",
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_indent_higher,
        tooltip = stringResource(R.string.higher_indent),
        active = false,
        tag = "",
    ),
    EditorToolbarItem.Separator,
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_brackets,
        tooltip = stringResource(R.string.brackets),
        active = false,
        tag = "",
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_square_brakcets,
        tooltip = stringResource(R.string.square_brackets),
        active = false,
        tag = "",
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_curly_brakcets,
        tooltip = stringResource(R.string.curly_brackets),
        active = false,
        tag = "",
    ),
    EditorToolbarItem.Separator,
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_code_block,
        tooltip = stringResource(R.string.code_block),
        active = activeStyles.any { it is Style.CodeBlock },
        tag = CodeBlockProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_blockquote,
        tooltip = stringResource(R.string.blockquote),
        active = activeStyles.any { it is Style.BlockQuote },
        tag = BlockQuoteProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_callout,
        tooltip = stringResource(R.string.callout),
        active = activeStyles.any { it is Style.Callout },
        tag = BlockQuoteProcessor.TAG_CALLOUT,
    ),
    EditorToolbarItem.Separator,
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_link,
        tooltip = stringResource(R.string.link),
        active = activeStyles.any { it is Style.InternalLink || it is Style.InlineLink },
        tag = InternalLinkProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_image,
        tooltip = stringResource(R.string.image),
        active = activeStyles.any { it is Style.Image },
        tag = ImageProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_footnote,
        tooltip = stringResource(R.string.footnote),
        active = activeStyles.any { it is Style.FootnoteLink },
        tag = FootnoteLinkProcessor.TAG,
    ),
    EditorToolbarItem.Separator,
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_undo,
        tooltip = stringResource(R.string.undo),
        active = false,
        tag = "",
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_redo,
        tooltip = stringResource(R.string.redo),
        active = false,
        tag = "",
    ),
)
