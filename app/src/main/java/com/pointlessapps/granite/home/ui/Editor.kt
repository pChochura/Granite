package com.pointlessapps.granite.home.ui

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.pointlessapps.granite.R
import com.pointlessapps.granite.home.utils.NoOpBringIntoViewSpec
import com.pointlessapps.granite.ui.components.ComposeMarkdownTextField
import com.pointlessapps.granite.ui.components.ComposeTextField
import com.pointlessapps.granite.ui.components.defaultComposeTextFieldStyle
import kotlinx.coroutines.flow.collect
import kotlin.coroutines.cancellation.CancellationException
import com.pointlessapps.granite.ui.R as RC

@Composable
@OptIn(ExperimentalFoundationApi::class)
internal fun Editor(
    viewModel: HomeViewModel,
    contentPadding: PaddingValues,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val editorFocusRequester = remember { FocusRequester() }
    var crossfadeProgress by remember { mutableFloatStateOf(0f) }
    var previousNoteTitle by remember { mutableStateOf(TextFieldValue()) }
    var previousNoteContent by remember { mutableStateOf(TextFieldValue()) }

    PredictiveBackHandler(viewModel.peekFileFromStack() != null) { progress ->
        val nextItem = viewModel.peekFileFromStack() ?: return@PredictiveBackHandler progress.collect()
        previousNoteTitle = TextFieldValue(nextItem.name)
        previousNoteContent = TextFieldValue(nextItem.content.orEmpty())
        try {
            progress.collect { event -> crossfadeProgress = event.progress }
            viewModel.openFileFromStack()
        } catch (e: CancellationException) {
            e.printStackTrace()
        }

        crossfadeProgress = 0f
    }

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
            Box {
                Title(
                    title = viewModel.state.noteTitle,
                    onTitleChanged = viewModel::onNoteTitleChanged,
                    alpha = 1f - crossfadeProgress,
                )

                if (crossfadeProgress > 0) {
                    Title(
                        title = previousNoteTitle,
                        onTitleChanged = {},
                        alpha = crossfadeProgress,
                    )
                }
            }
            Box {
                Content(
                    content = viewModel.state.noteContent,
                    onContentChanged = viewModel::onNoteContentChanged,
                    editorFocusRequester = editorFocusRequester,
                    alpha = 1f - crossfadeProgress,
                )

                if (crossfadeProgress > 0) {
                    Content(
                        content = previousNoteContent,
                        onContentChanged = {},
                        editorFocusRequester = editorFocusRequester,
                        alpha = crossfadeProgress,
                    )
                }
            }
        }
    }
}

@Composable
private fun Title(
    title: TextFieldValue,
    onTitleChanged: (TextFieldValue) -> Unit,
    alpha: Float,
) {
    ComposeTextField(
        modifier = Modifier
            .graphicsLayer { this.alpha = alpha }
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(RC.dimen.margin_semi_big),
                vertical = dimensionResource(RC.dimen.margin_tiny),
            ),
        value = title,
        onValueChange = onTitleChanged,
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
}

@Composable
private fun Content(
    content: TextFieldValue,
    onContentChanged: (TextFieldValue) -> Unit,
    editorFocusRequester: FocusRequester,
    alpha: Float,
) {
    ComposeMarkdownTextField(
        modifier = Modifier
            .graphicsLayer { this.alpha = alpha }
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
