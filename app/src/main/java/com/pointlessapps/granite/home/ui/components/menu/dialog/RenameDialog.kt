package com.pointlessapps.granite.home.ui.components.menu.dialog

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.granite.R
import com.pointlessapps.granite.ui.components.ComposeButton
import com.pointlessapps.granite.ui.components.ComposeDialog
import com.pointlessapps.granite.ui.components.ComposeDialogDismissible
import com.pointlessapps.granite.ui.components.ComposeTextField
import com.pointlessapps.granite.ui.components.defaultComposeButtonStyle
import com.pointlessapps.granite.ui.components.defaultComposeButtonTextStyle
import com.pointlessapps.granite.ui.components.defaultComposeDialogStyle
import com.pointlessapps.granite.ui.components.defaultComposeTextFieldStyle
import com.pointlessapps.granite.ui.R as RC

@Composable
internal fun RenameDialog(
    data: RenameDialogData,
    onNameChanged: (TextFieldValue) -> Unit,
    onSaveClicked: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val nameFocusRequester = remember { FocusRequester() }
    LaunchedEffect(nameFocusRequester) {
        nameFocusRequester.requestFocus()
    }

    ComposeDialog(
        onDismissRequest = onDismissRequest,
        dialogStyle = defaultComposeDialogStyle().copy(
            label = stringResource(R.string.rename),
            iconRes = RC.drawable.ic_edit,
            dismissible = ComposeDialogDismissible.OnBackPress,
        ),
    ) {
        ComposeTextField(
            value = data.name,
            onValueChange = { onNameChanged(it) },
            modifier = Modifier
                .focusRequester(nameFocusRequester)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = MaterialTheme.shapes.small,
                )
                .border(
                    width = dimensionResource(RC.dimen.default_border_width),
                    color = MaterialTheme.colorScheme.outlineVariant,
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
                placeholder = stringResource(R.string.new_name),
                textColor = MaterialTheme.colorScheme.onSurface,
                placeholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            ),
        )

        val enabled = data.name.text.isNotBlank()
        val alpha by animateFloatAsState(if (enabled) 1f else 0.3f)
        ComposeButton(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(alpha),
            label = stringResource(R.string.save),
            onClick = onSaveClicked,
            buttonStyle = defaultComposeButtonStyle().copy(
                enabled = enabled,
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primary,
                textStyle = defaultComposeButtonTextStyle().copy(
                    textAlign = TextAlign.Center,
                    textColor = MaterialTheme.colorScheme.onPrimary,
                    disabledTextColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ),
        )
    }
}

internal data class RenameDialogData(
    val name: TextFieldValue,
    val id: Int,
)
