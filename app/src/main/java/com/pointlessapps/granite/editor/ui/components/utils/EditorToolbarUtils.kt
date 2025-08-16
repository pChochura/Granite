package com.pointlessapps.granite.editor.ui.components.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pointlessapps.granite.R
import com.pointlessapps.granite.editor.ui.components.EditorToolbarItem
import com.pointlessapps.granite.markdown.renderer.assist.EditingAssist
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
import com.pointlessapps.granite.markdown.renderer.processors.InlineLinkProcessor
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
            activeStyles.filter { it.tag == HeaderProcessor.TAG }
                .maxOfOrNull { it.arg?.toIntOrNull() ?: 1 } ?: 1,
        ),
        lastActiveStyle = activeStyles.lastOrNull { it.tag == HeaderProcessor.TAG },
        tag = HeaderProcessor.TAG,
    ),
    EditorToolbarItem.Separator,
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_bold,
        tooltip = stringResource(R.string.bold),
        lastActiveStyle = activeStyles.lastOrNull { it.tag == BoldProcessor.TAG },
        tag = BoldProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_italic,
        tooltip = stringResource(R.string.italic),
        lastActiveStyle = activeStyles.lastOrNull { it.tag == ItalicProcessor.TAG },
        tag = ItalicProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_strikethrough,
        tooltip = stringResource(R.string.strikethrough),
        lastActiveStyle = activeStyles.lastOrNull { it.tag == StrikethroughProcessor.TAG },
        tag = StrikethroughProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_highlight,
        tooltip = stringResource(R.string.highlight),
        lastActiveStyle = activeStyles.lastOrNull { it.tag == HighlightProcessor.TAG },
        tag = HighlightProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_code_span,
        tooltip = stringResource(R.string.code_span),
        lastActiveStyle = activeStyles.lastOrNull { it.tag == CodeSpanProcessor.TAG },
        tag = CodeSpanProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_comment,
        tooltip = stringResource(R.string.comment),
        lastActiveStyle = activeStyles.lastOrNull { it.tag == CommentProcessor.TAG },
        tag = CommentProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_tag,
        tooltip = stringResource(R.string.tag),
        lastActiveStyle = activeStyles.lastOrNull { it.tag == HashtagProcessor.TAG },
        tag = HashtagProcessor.TAG,
    ),
    EditorToolbarItem.Separator,
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_ordered_list,
        tooltip = stringResource(R.string.ordered_list),
        lastActiveStyle = activeStyles.lastOrNull { it.tag == OrderedListProcessor.TAG },
        tag = OrderedListProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_unordered_list,
        tooltip = stringResource(R.string.unordered_list),
        lastActiveStyle = activeStyles.lastOrNull { it.tag == UnorderedListProcessor.TAG },
        tag = UnorderedListProcessor.TAG,
    ),
    EditorToolbarItem.Separator,
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_indent_lower,
        tooltip = stringResource(R.string.lower_indent),
        lastActiveStyle = null,
        tag = EditingAssist.TAG_LOWER_INDENT,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_indent_higher,
        tooltip = stringResource(R.string.higher_indent),
        lastActiveStyle = null,
        tag = EditingAssist.TAG_HIGHER_INDENT,
    ),
    EditorToolbarItem.Separator,
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_code_block,
        tooltip = stringResource(R.string.code_block),
        lastActiveStyle = activeStyles.lastOrNull { it.tag == CodeBlockProcessor.TAG },
        tag = CodeBlockProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_blockquote,
        tooltip = stringResource(R.string.blockquote),
        lastActiveStyle = activeStyles.lastOrNull { it.tag == BlockQuoteProcessor.TAG },
        tag = BlockQuoteProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_callout,
        tooltip = stringResource(R.string.callout),
        lastActiveStyle = activeStyles.lastOrNull { it.tag == BlockQuoteProcessor.TAG_CALLOUT },
        tag = BlockQuoteProcessor.TAG_CALLOUT,
    ),
    EditorToolbarItem.Separator,
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_link,
        tooltip = stringResource(R.string.link),
        lastActiveStyle = activeStyles.lastOrNull {
            it.tag == InternalLinkProcessor.TAG || it.tag == InlineLinkProcessor.TAG
        },
        tag = InternalLinkProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_image,
        tooltip = stringResource(R.string.image),
        lastActiveStyle = activeStyles.lastOrNull { it.tag == ImageProcessor.TAG },
        tag = ImageProcessor.TAG,
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_footnote,
        tooltip = stringResource(R.string.footnote),
        lastActiveStyle = activeStyles.lastOrNull { it.tag == FootnoteLinkProcessor.TAG },
        tag = FootnoteLinkProcessor.TAG,
    ),
    EditorToolbarItem.Separator,
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_undo,
        tooltip = stringResource(R.string.undo),
        lastActiveStyle = null,
        tag = "",
    ),
    EditorToolbarItem.Simple(
        iconRes = R.drawable.icon_redo,
        tooltip = stringResource(R.string.redo),
        lastActiveStyle = null,
        tag = "",
    ),
)
