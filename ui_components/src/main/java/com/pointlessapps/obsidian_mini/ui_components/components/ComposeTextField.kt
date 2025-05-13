package com.project.ui_components.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ComposeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onImeAction: ((ImeAction) -> Unit)? = null,
    textFieldStyle: ComposeTextFieldStyle = defaultComposeTextFieldStyle(),
) {
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.copy(text = value)

    ComposeTextField(
        value = textFieldValue,
        onValueChange = {
            textFieldValueState = it
            if (value != it.text) {
                onValueChange(it.text)
            }
        },
        modifier = modifier,
        onImeAction = onImeAction,
        textFieldStyle = textFieldStyle,
    )
}

@Composable
fun ComposeTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    onImeAction: ((ImeAction) -> Unit)? = null,
    textFieldStyle: ComposeTextFieldStyle = defaultComposeTextFieldStyle(),
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = modifier) {
        if (value.text.isEmpty()) {
            ComposeText(
                modifier = Modifier.fillMaxWidth(),
                text = textFieldStyle.placeholder,
                textStyle = defaultComposeTextStyle().copy(
                    typography = textFieldStyle.textStyle,
                    textColor = textFieldStyle.placeholderColor,
                    textAlign = textFieldStyle.textAlign,
                    maxLines = textFieldStyle.maxLines,
                ),
            )
        }
        BasicTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = textFieldStyle.keyboardOptions,
            keyboardActions = composeTextFieldKeyboardActions {
                defaultKeyboardAction(it)
                onImeAction?.invoke(it)
                if (it == ImeAction.Done) {
                    keyboardController?.hide()
                }
            },
            visualTransformation = textFieldStyle.visualTransformation,
            textStyle = textFieldStyle.textStyle.copy(
                color = textFieldStyle.textColor,
                textAlign = textFieldStyle.textAlign,
            ),
            cursorBrush = SolidColor(textFieldStyle.cursorColor),
            maxLines = textFieldStyle.maxLines,
        )
    }
}

private fun composeTextFieldKeyboardActions(onAny: KeyboardActionScope.(ImeAction) -> Unit) =
    KeyboardActions(
        onDone = { onAny(ImeAction.Done) },
        onGo = { onAny(ImeAction.Go) },
        onNext = { onAny(ImeAction.Next) },
        onPrevious = { onAny(ImeAction.Previous) },
        onSearch = { onAny(ImeAction.Search) },
        onSend = { onAny(ImeAction.Send) },
    )

@Composable
fun defaultComposeTextFieldStyle() = ComposeTextFieldStyle(
    keyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.Sentences,
    ),
    visualTransformation = VisualTransformation.None,
    placeholder = "",
    textStyle = MaterialTheme.typography.bodyLarge,
    textColor = MaterialTheme.colorScheme.onSurface,
    textAlign = TextAlign.Left,
    maxLines = Int.MAX_VALUE,
    placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = MaterialTheme.colorScheme.onSurfaceVariant,
)

data class ComposeTextFieldStyle(
    val keyboardOptions: KeyboardOptions,
    val visualTransformation: VisualTransformation,
    val placeholder: String,
    val textStyle: TextStyle,
    val textColor: Color,
    val textAlign: TextAlign,
    val maxLines: Int,
    val placeholderColor: Color,
    val cursorColor: Color,
)
