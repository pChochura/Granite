package com.project.compose.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation

@Suppress("unused")
@Composable
internal fun ComposeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onImeAction: ((ImeAction) -> Unit)? = null,
    textFieldModel: ComposeTextFieldModel = defaultComposeTextFieldModel(),
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
        textFieldModel = textFieldModel,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun ComposeTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    onImeAction: ((ImeAction) -> Unit)? = null,
    textFieldModel: ComposeTextFieldModel = defaultComposeTextFieldModel(),
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = modifier) {
        if (value.text.isEmpty()) {
            ComposeText(
                modifier = Modifier.fillMaxSize(),
                text = textFieldModel.placeholder,
                textStyle = defaultComposeTextStyle().copy(
                    typography = textFieldModel.textStyle,
                    textColor = textFieldModel.placeholderColor,
                ),
            )
        }
        BasicTextField(
            modifier = Modifier.fillMaxSize(),
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = textFieldModel.keyboardOptions,
            keyboardActions = composeTextFieldKeyboardActions {
                defaultKeyboardAction(it)
                onImeAction?.invoke(it)
                if (it == ImeAction.Done) {
                    keyboardController?.hide()
                }
            },
            visualTransformation = textFieldModel.visualTransformation,
            textStyle = textFieldModel.textStyle.copy(
                textFieldModel.textColor,
            ),
            cursorBrush = SolidColor(textFieldModel.cursorColor),
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
        onSend = { onAny(ImeAction.Search) },
    )

@Composable
internal fun defaultComposeTextFieldModel() = ComposeTextFieldModel(
    keyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.Sentences,
    ),
    visualTransformation = VisualTransformation.None,
    placeholder = "",
    textStyle = MaterialTheme.typography.body1,
    textColor = MaterialTheme.colors.onPrimary,
    placeholderColor = MaterialTheme.colors.secondaryVariant,
    cursorColor = MaterialTheme.colors.secondary,
)

internal data class ComposeTextFieldModel(
    val keyboardOptions: KeyboardOptions,
    val visualTransformation: VisualTransformation,
    val placeholder: String,
    val textStyle: TextStyle,
    val textColor: Color,
    val placeholderColor: Color,
    val cursorColor: Color,
)
