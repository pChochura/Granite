package com.pointlessapps.granite.home.ui.components.menu.dialog

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.granite.ui.components.ComposeButton
import com.pointlessapps.granite.ui.components.ComposeDialog
import com.pointlessapps.granite.ui.components.ComposeDialogDismissible
import com.pointlessapps.granite.ui.components.ComposeText
import com.pointlessapps.granite.ui.components.defaultComposeButtonStyle
import com.pointlessapps.granite.ui.components.defaultComposeButtonTextStyle
import com.pointlessapps.granite.ui.components.defaultComposeDialogStyle
import com.pointlessapps.granite.ui.components.defaultComposeTextStyle
import com.pointlessapps.granite.ui.R as RC

@Composable
internal fun ConfirmationDialog(
    data: ConfirmationDialogData,
    onDismissRequest: () -> Unit,
) {
    ComposeDialog(
        onDismissRequest = onDismissRequest,
        dialogStyle = defaultComposeDialogStyle().copy(
            label = stringResource(data.title),
            iconRes = RC.drawable.ic_warning,
            dismissible = ComposeDialogDismissible.OnBackPress,
        ),
    ) {
        ComposeText(
            text = stringResource(data.description),
            textStyle = defaultComposeTextStyle().copy(
                typography = MaterialTheme.typography.bodyMedium,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            ),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_medium)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ComposeButton(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(data.confirmText),
                onClick = {
                    data.onConfirmClicked()
                    onDismissRequest()
                },
                buttonStyle = defaultComposeButtonStyle().copy(
                    containerColor = MaterialTheme.colorScheme.primary,
                    textStyle = defaultComposeButtonTextStyle().copy(
                        textAlign = TextAlign.Center,
                        textColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ),
            )
            ComposeButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = dimensionResource(RC.dimen.default_border_width),
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape,
                    ),
                label = stringResource(data.cancelText),
                onClick = {
                    data.onCancelClicked()
                    onDismissRequest()
                },
                buttonStyle = defaultComposeButtonStyle().copy(
                    containerColor = Color.Transparent,
                    textStyle = defaultComposeButtonTextStyle().copy(
                        textAlign = TextAlign.Center,
                        textColor = MaterialTheme.colorScheme.onSurface,
                    ),
                ),
            )
        }
    }
}

internal data class ConfirmationDialogData(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @StringRes val confirmText: Int,
    @StringRes val cancelText: Int,
    val onConfirmClicked: () -> Unit,
    val onCancelClicked: () -> Unit,
)
