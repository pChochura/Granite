package com.pointlessapps.granite.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import com.pointlessapps.granite.ui.R

@Composable
fun ComposeSnackbar(
    message: String,
    actionLabel: String?,
    actionCallback: (() -> Unit)?,
    onDismissRequest: () -> Unit,
) {
    Snackbar(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onDismissRequest),
        shape = MaterialTheme.shapes.small,
        containerColor = MaterialTheme.colorScheme.inverseSurface,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = dimensionResource(id = R.dimen.margin_small),
                alignment = Alignment.CenterHorizontally,
            ),
        ) {
            ComposeIcon(
                iconRes = R.drawable.ic_warning,
                modifier = Modifier.size(dimensionResource(id = R.dimen.button_icon_size)),
                iconStyle = defaultComposeIconStyle().copy(
                    tint = MaterialTheme.colorScheme.inverseOnSurface,
                ),
            )
            ComposeText(
                modifier = Modifier.weight(1f),
                text = message,
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colorScheme.inverseOnSurface,
                    typography = MaterialTheme.typography.bodyMedium,
                ),
            )

            if (actionLabel != null) {
                ComposeText(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable { actionCallback?.invoke() }
                        .padding(dimensionResource(id = R.dimen.margin_tiny)),
                    text = actionLabel.uppercase(),
                    textStyle = defaultComposeTextStyle().copy(
                        textColor = MaterialTheme.colorScheme.inversePrimary,
                        typography = MaterialTheme.typography.labelLarge,
                    ),
                )
            }
        }
    }
}

class ComposeSnackbarHostState(private val onShowSnackbarListener: SnackbarHostListener) {

    fun showSnackbar(
        @StringRes message: Int,
        @StringRes actionLabel: Int? = null,
        actionCallback: (() -> Unit)? = null,
        dismissCallback: (() -> Unit)? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
    ) = onShowSnackbarListener.showSnackbar(
        message = message,
        actionLabel = actionLabel,
        actionCallback = actionCallback,
        dismissCallback = dismissCallback,
        duration = duration,
    )

    interface SnackbarHostListener {
        fun showSnackbar(
            @StringRes message: Int,
            @StringRes actionLabel: Int?,
            actionCallback: (() -> Unit)?,
            dismissCallback: (() -> Unit)?,
            duration: SnackbarDuration,
        )
    }
}
