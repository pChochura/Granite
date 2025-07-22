package com.pointlessapps.granite.editor.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import com.pointlessapps.granite.ui.components.ComposeMarkdownTextField
import com.pointlessapps.granite.ui.components.ComposeTextField
import com.pointlessapps.granite.ui.components.defaultComposeTextFieldStyle
import com.pointlessapps.granite.ui.R as RC

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun EditorContent(
    contentPadding: PaddingValues,
    title: TextFieldValue,
    onTitleChanged: (TextFieldValue) -> Unit,
    content: TextFieldValue,
    onContentChanged: (TextFieldValue) -> Unit,
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
            )

            // TODO add properties list

            Content(
                content = content,
                onContentChanged = onContentChanged,
                editorFocusRequester = editorFocusRequester,
            )
        }
    }
}

@Composable
private fun Title(
    title: TextFieldValue,
    onTitleChanged: (TextFieldValue) -> Unit,
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
        ),
    )
}

@Composable
private fun Content(
    content: TextFieldValue,
    onContentChanged: (TextFieldValue) -> Unit,
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
        textFieldStyle = defaultComposeTextFieldStyle(),
    )
}
