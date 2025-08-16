package com.pointlessapps.granite.editor.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import com.pointlessapps.granite.R
import com.pointlessapps.granite.editor.ui.components.utils.getEditorToolbarItems
import com.pointlessapps.granite.home.utils.NoOpBringIntoViewSpec
import com.pointlessapps.granite.markdown.renderer.assist.EditingAssist
import com.pointlessapps.granite.markdown.renderer.assist.Style
import com.pointlessapps.granite.markdown.renderer.processors.CodeBlockProcessor
import com.pointlessapps.granite.model.DateProperty
import com.pointlessapps.granite.model.ListProperty
import com.pointlessapps.granite.model.Property
import com.pointlessapps.granite.ui.components.ComposeIcon
import com.pointlessapps.granite.ui.components.ComposeMarkdownTextField
import com.pointlessapps.granite.ui.components.ComposeText
import com.pointlessapps.granite.ui.components.ComposeTextField
import com.pointlessapps.granite.ui.components.defaultComposeIconStyle
import com.pointlessapps.granite.ui.components.defaultComposeTextFieldStyle
import com.pointlessapps.granite.ui.components.defaultComposeTextStyle
import com.pointlessapps.granite.utils.applyIf
import java.util.Date
import com.pointlessapps.granite.ui.R as RC

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
internal fun EditorContent(
    contentPadding: PaddingValues,
    title: TextFieldValue,
    onTitleChanged: (TextFieldValue) -> Unit,
    properties: List<Property>,
    content: TextFieldValue,
    onContentChanged: (TextFieldValue) -> Unit,
    readOnlyTitle: Boolean,
    onRunCodeBlock: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val editorFocusRequester = remember { FocusRequester() }

    var transformedText by remember {
        mutableStateOf(TransformedText(content.annotatedString, OffsetMapping.Identity))
    }
    val activeStyles by remember(transformedText) {
        mutableStateOf(
            EditingAssist.process(
                transformedText.text,
                transformedText.offsetMapping.mapSelection(content.selection),
            ),
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        CompositionLocalProvider(LocalBringIntoViewSpec provides NoOpBringIntoViewSpec) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding)
                    .padding(top = dimensionResource(RC.dimen.margin_medium))
                    .clickable(
                        interactionSource = null,
                        indication = null,
                        onClick = {
                            editorFocusRequester.requestFocus()
                            keyboardController?.show()
                        },
                    )
                    .padding(bottom = dimensionResource(R.dimen.editor_bottom_padding)),
            ) {
                Title(
                    title = title,
                    onTitleChanged = onTitleChanged,
                    readOnly = readOnlyTitle,
                )

                PropertiesList(properties = properties)

                Content(
                    content = content,
                    onContentChanged = onContentChanged,
                    onTransformedTextChange = { transformedText = it },
                    onRunCodeBlock = onRunCodeBlock,
                    editorFocusRequester = editorFocusRequester,
                    activeStyles = activeStyles,
                )

            }
        }

        AnimatedVisibility(
            visible = WindowInsets.isImeVisible,
            modifier = Modifier.imePadding(),
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            content = {
                EditorToolbar(
                    activeStyles = activeStyles,
                    onApplyStyle = {
                        onContentChanged(
                            EditingAssist.applyStyle(
                                content = content,
                                lastActiveStyle = it.lastActiveStyle,
                                tag = it.tag,
                            ),
                        )
                    },
                )
            },
        )
    }
}

@Composable
private fun Title(
    title: TextFieldValue,
    onTitleChanged: (TextFieldValue) -> Unit,
    readOnly: Boolean,
) {
    ComposeTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(RC.dimen.margin_semi_big),
                vertical = dimensionResource(RC.dimen.margin_tiny),
            ),
        value = title,
        onValueChange = onTitleChanged,
        textFieldStyle = defaultComposeTextFieldStyle().copy(
            textStyle = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrectEnabled = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                showKeyboardOnFocus = true,
            ),
            placeholder = stringResource(R.string.title),
            placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
            readOnly = readOnly,
        ),
    )
}

@Composable
private fun PropertiesList(
    properties: List<Property>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(RC.dimen.margin_semi_big),
                vertical = dimensionResource(RC.dimen.margin_tiny),
            ),
    ) {
        properties.forEach { property ->
            PropertyItem(property = property)
        }
    }
}

@Composable
private fun PropertyItem(property: Property) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(RC.dimen.margin_nano)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_tiny)),
    ) {
        ComposeIcon(
            modifier = Modifier.size(dimensionResource(RC.dimen.caption_icon_size)),
            iconRes = property.icon,
            iconStyle = defaultComposeIconStyle().copy(
                tint = MaterialTheme.colorScheme.onBackground,
            )
        )

        ComposeText(
            modifier = Modifier.weight(1f),
            text = stringResource(property.name),
            textStyle = defaultComposeTextStyle().copy(
                textColor = MaterialTheme.colorScheme.onBackground,
                typography = MaterialTheme.typography.labelMedium,
            )
        )

        when (property) {
            is DateProperty -> ComposeText(
                modifier = Modifier.weight(2f),
                text = stringResource(R.string.date_absolute, Date(property.date)),
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colorScheme.onBackground,
                    typography = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                )
            )

            is ListProperty -> LazyRow(
                modifier = Modifier.weight(2f),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_tiny)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items(property.items, key = { it.id }) {
                    val color = Color(it.color)
                    ComposeText(
                        text = it.name,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(color)
                            .padding(
                                vertical = dimensionResource(RC.dimen.margin_nano),
                                horizontal = dimensionResource(RC.dimen.margin_tiny),
                            ),
                        textStyle = defaultComposeTextStyle().copy(
                            textColor = if (color.luminance() > 0.5) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onPrimary
                            },
                            typography = MaterialTheme.typography.labelSmall,
                        ),
                    )
                }

                item {
                    ComposeIcon(
                        modifier = Modifier.size(dimensionResource(RC.dimen.caption_icon_size)),
                        iconRes = RC.drawable.ic_plus,
                        iconStyle = defaultComposeIconStyle().copy(
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun Content(
    content: TextFieldValue,
    onContentChanged: (TextFieldValue) -> Unit,
    onTransformedTextChange: (TransformedText) -> Unit,
    onRunCodeBlock: (String) -> Unit,
    editorFocusRequester: FocusRequester,
    activeStyles: List<Style>,
) {
    val insideCodeBlock = activeStyles.any { it.tag == CodeBlockProcessor.TAG }

    ComposeMarkdownTextField(
        modifier = Modifier
            .focusRequester(editorFocusRequester)
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(RC.dimen.margin_semi_big),
                vertical = dimensionResource(RC.dimen.margin_tiny),
            ),
        value = content,
        onValueChange = onContentChanged,
        onTransformedTextChange = onTransformedTextChange,
        onRunCodeBlock = onRunCodeBlock,
        textFieldStyle = defaultComposeTextFieldStyle().copy(
            placeholder = stringResource(R.string.content),
            placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = !insideCodeBlock,
                capitalization = if (insideCodeBlock) {
                    KeyboardCapitalization.None
                } else {
                    KeyboardCapitalization.Sentences
                },
            ),
        ),
    )
}

@Composable
private fun EditorToolbar(
    activeStyles: List<Style>,
    onApplyStyle: (EditorToolbarItem) -> Unit,
) {
    val editorToolbarItems = getEditorToolbarItems(activeStyles)

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = MaterialTheme.shapes.medium.copy(
                    bottomStart = CornerSize(0),
                    bottomEnd = CornerSize(0),
                ),
            )
            .border(
                width = dimensionResource(RC.dimen.default_border_width),
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.medium.copy(
                    bottomStart = CornerSize(0),
                    bottomEnd = CornerSize(0),
                ),
            ),
        contentPadding = PaddingValues(
            horizontal = dimensionResource(RC.dimen.margin_medium),
            vertical = dimensionResource(RC.dimen.margin_tiny),
        ),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_tiny)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(editorToolbarItems) {
            if (it is EditorToolbarItem.Separator) {
                VerticalDivider(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.editor_toolbar_separator_height)),
                    thickness = dimensionResource(RC.dimen.default_border_width),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            } else {
                EditorToolbarItem(
                    item = it,
                    onClicked = { onApplyStyle(it) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorToolbarItem(
    item: EditorToolbarItem,
    onClicked: () -> Unit,
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { PlainTooltip { Text(item.tooltip) } },
        state = rememberTooltipState(),
        focusable = false,
    ) {
        Row(
            modifier = Modifier
                .height(dimensionResource(R.dimen.editor_toolbar_item_height))
                .clip(MaterialTheme.shapes.small)
                .clickable(
                    role = Role.Button,
                    onClickLabel = item.tooltip,
                    onClick = onClicked,
                )
                .applyIf(item.lastActiveStyle != null) {
                    background(MaterialTheme.colorScheme.outlineVariant)
                }
                .padding(horizontal = dimensionResource(RC.dimen.margin_nano)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ComposeIcon(
                modifier = Modifier.size(dimensionResource(R.dimen.editor_toolbar_item_icon_height)),
                iconRes = item.iconRes,
                iconStyle = defaultComposeIconStyle().copy(
                    tint = MaterialTheme.colorScheme.onSurface,
                ),
            )

            if (item is EditorToolbarItem.Extended) {
                ComposeText(
                    text = item.tooltip,
                    textStyle = defaultComposeTextStyle().copy(
                        textColor = MaterialTheme.colorScheme.onSurface,
                        typography = MaterialTheme.typography.labelSmall,
                    ),
                )
            }
        }
    }
}

private fun OffsetMapping.mapSelection(selection: TextRange): TextRange {
    return TextRange(
        start = originalToTransformed(selection.start),
        end = originalToTransformed(selection.end),
    )
}
