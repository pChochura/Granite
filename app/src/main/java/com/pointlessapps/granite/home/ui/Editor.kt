package com.pointlessapps.granite.home.ui

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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.pointlessapps.granite.R
import com.pointlessapps.granite.home.utils.NoOpBringIntoViewSpec
import com.pointlessapps.granite.ui.components.ComposeMarkdownTextField
import com.pointlessapps.granite.ui.components.ComposeTextField
import com.pointlessapps.granite.ui.components.defaultComposeTextFieldStyle
import com.pointlessapps.granite.ui.R as RC

@Composable
@OptIn(ExperimentalFoundationApi::class)
internal fun Editor(
    viewModel: HomeViewModel,
    contentPadding: PaddingValues,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val editorFocusRequester = remember { FocusRequester() }

    CompositionLocalProvider(LocalBringIntoViewSpec provides NoOpBringIntoViewSpec) {
        Column(
            modifier = Modifier
                .onFocusChanged {
                    if (!it.hasFocus) {
                        viewModel.saveNote(silently = true)
                    }
                }
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
            ComposeTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimensionResource(RC.dimen.margin_semi_big),
                        vertical = dimensionResource(RC.dimen.margin_tiny),
                    ),
                value = viewModel.state.noteTitle,
                onValueChange = viewModel::onNoteTitleChanged,
                textFieldStyle = defaultComposeTextFieldStyle().copy(
                    textStyle = MaterialTheme.typography.titleLarge,
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

            ComposeMarkdownTextField(
                modifier = Modifier
                    .focusRequester(editorFocusRequester)
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimensionResource(RC.dimen.margin_semi_big),
                        vertical = dimensionResource(RC.dimen.margin_tiny),
                    ),
                value = viewModel.state.noteContent,
                onValueChange = viewModel::onNoteContentChanged,
                textFieldStyle = defaultComposeTextFieldStyle(),
            )
        }
    }
}
