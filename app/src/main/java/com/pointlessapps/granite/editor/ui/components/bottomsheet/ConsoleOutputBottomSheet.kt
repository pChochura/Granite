package com.pointlessapps.granite.editor.ui.components.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.granite.R
import com.pointlessapps.granite.ui.components.ComposeText
import com.pointlessapps.granite.ui.components.ComposeTextField
import com.pointlessapps.granite.ui.components.defaultComposeTextFieldStyle
import com.pointlessapps.granite.ui.components.defaultComposeTextStyle
import com.pointlessapps.granite.ui.R as RC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ConsoleOutputBottomSheet(
    state: SheetState,
    isLoading: Boolean,
    acceptsInput: Boolean,
    output: List<String>,
    onInputCallback: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        scrimColor = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
        dragHandle = null,
        onDismissRequest = onDismissRequest,
        sheetState = state,
    ) {
        Column(
            modifier = Modifier.padding(
                vertical = dimensionResource(RC.dimen.margin_semi_big),
                horizontal = dimensionResource(RC.dimen.margin_semi_big),
            ),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_tiny)),
        ) {
            ComposeText(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.console_output),
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colorScheme.onSurface,
                    typography = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                ),
            )
            Spacer(Modifier.height(dimensionResource(RC.dimen.margin_tiny)))

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = dimensionResource(R.dimen.console_min_height))
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
                contentPadding = PaddingValues(dimensionResource(RC.dimen.margin_tiny)),
            ) {
                items(output) { line ->
                    ComposeText(
                        modifier = Modifier.fillMaxWidth(),
                        text = line,
                        textStyle = defaultComposeTextStyle().copy(
                            textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            typography = MaterialTheme.typography.bodySmall,
                        ),
                    )
                }

                if (isLoading) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier.size(dimensionResource(R.dimen.console_loader_size)),
                            strokeWidth = dimensionResource(R.dimen.console_loader_stroke),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                if (acceptsInput) {
                    item {
                        var input by remember { mutableStateOf("") }
                        ComposeTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = input,
                            onValueChange = { input = it },
                            textFieldStyle = defaultComposeTextFieldStyle().copy(
                                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                textStyle = MaterialTheme.typography.bodySmall,
                                placeholder = stringResource(R.string.provide_input),
                                placeholderColor = MaterialTheme.colorScheme
                                    .onSurfaceVariant.copy(alpha = 0.3f),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done,
                                ),
                            ),
                            onImeAction = {
                                if (it == ImeAction.Done) {
                                    onInputCallback(input)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
