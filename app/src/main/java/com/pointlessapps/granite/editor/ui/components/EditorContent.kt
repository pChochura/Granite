package com.pointlessapps.granite.editor.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.pointlessapps.granite.R
import com.pointlessapps.granite.home.utils.NoOpBringIntoViewSpec
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
import java.util.Date
import com.pointlessapps.granite.ui.R as RC

@OptIn(ExperimentalFoundationApi::class)
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

    CompositionLocalProvider(LocalBringIntoViewSpec provides NoOpBringIntoViewSpec) {
        Column(
            modifier = Modifier
                .padding(top = dimensionResource(RC.dimen.margin_medium))
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
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
                onRunCodeBlock = onRunCodeBlock,
                editorFocusRequester = editorFocusRequester,
            )
        }
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
    onRunCodeBlock: (String) -> Unit,
    editorFocusRequester: FocusRequester,
) {
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
        onRunCodeBlock = onRunCodeBlock,
        textFieldStyle = defaultComposeTextFieldStyle().copy(
            placeholder = stringResource(R.string.content),
            placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
        ),
    )
}
