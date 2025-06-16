package com.pointlessapps.granite.home.ui.menu.dialog

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.pointlessapps.granite.R
import com.pointlessapps.granite.ui.R as RC
import com.pointlessapps.granite.ui.components.ComposeButton
import com.pointlessapps.granite.ui.components.ComposeDialog
import com.pointlessapps.granite.ui.components.ComposeDialogDismissible
import com.pointlessapps.granite.ui.components.ComposeTextField
import com.pointlessapps.granite.ui.components.defaultComposeButtonStyle
import com.pointlessapps.granite.ui.components.defaultComposeButtonTextStyle
import com.pointlessapps.granite.ui.components.defaultComposeDialogStyle
import com.pointlessapps.granite.ui.components.defaultComposeTextFieldStyle

@Composable
internal fun CreateFolderDialog(
    data: CreateFolderDialogData,
    onNameChanged: (TextFieldValue) -> Unit,
    onSaveClicked: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val newFolderFocusRequester = remember { FocusRequester() }
    LaunchedEffect(newFolderFocusRequester) {
        newFolderFocusRequester.requestFocus()
    }

    ComposeDialog(
        onDismissRequest = onDismissRequest,
        dialogStyle = defaultComposeDialogStyle().copy(
            label = stringResource(R.string.create_folder),
            iconRes = RC.drawable.ic_add_folder,
            dismissible = ComposeDialogDismissible.OnBackPress,
        ),
    ) {
        ComposeTextField(
            value = data.name,
            onValueChange = { onNameChanged(it) },
            modifier = Modifier
                .focusRequester(newFolderFocusRequester)
                .fillMaxWidth()
                .border(
                    width = dimensionResource(RC.dimen.default_border_width),
                    color = MaterialTheme.colorScheme.outline,
                    shape = MaterialTheme.shapes.small,
                )
                .padding(dimensionResource(RC.dimen.margin_medium)),
            onImeAction = {
                if (it == ImeAction.Done) {
                    onSaveClicked()
                }
            },
            textFieldStyle = defaultComposeTextFieldStyle().copy(
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                    showKeyboardOnFocus = true,
                ),
                placeholder = stringResource(R.string.folder_name),
            ),
        )

        AnimatedContent(data.name.text.isNotBlank()) { enabled ->
            ComposeButton(
                label = stringResource(R.string.save),
                onClick = onSaveClicked,
                buttonStyle = defaultComposeButtonStyle().copy(
                    iconRes = RC.drawable.ic_done,
                    enabled = enabled,
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(0.1f),
                    textStyle = defaultComposeButtonTextStyle().copy(
                        textColor = if (enabled) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(0.38f)
                        },
                    ),
                ),
            )
        }
    }
}

internal data class CreateFolderDialogData(
    val name: TextFieldValue,
    val parentId: Int?,
)
