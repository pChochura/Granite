package com.pointlessapps.granite.ui_components.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pointlessapps.granite.ui_components.R

@Composable
fun ComposeDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dialogStyle: ComposeDialogStyle = defaultComposeDialogStyle(),
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = dialogStyle.dismissible.isDismissibleOnBackPress(),
            dismissOnClickOutside = dialogStyle.dismissible.isDismissibleOnClickOutside(),
        ),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        dimensionResource(id = R.dimen.medium_rounded_corners),
                    ),
                )
                .background(dialogStyle.containerColor)
                .padding(dimensionResource(id = R.dimen.margin_big)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_semi_big),
            ),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(id = R.dimen.margin_small),
                ),
            ) {
                if (dialogStyle.iconRes != null) {
                    ComposeIcon(
                        iconRes = dialogStyle.iconRes,
                        modifier = Modifier.size(dimensionResource(id = R.dimen.dialog_icon_size)),
                        iconStyle = defaultComposeIconStyle().copy(tint = dialogStyle.iconColor),
                    )
                }
                ComposeText(
                    text = dialogStyle.label,
                    textStyle = defaultComposeTextStyle().copy(
                        typography = MaterialTheme.typography.headlineSmall,
                        textColor = dialogStyle.textColor,
                    ),
                )
            }

            content()
        }
    }
}

@Composable
fun defaultComposeDialogStyle() = ComposeDialogStyle(
    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    textColor = MaterialTheme.colorScheme.onSurface,
    iconColor = MaterialTheme.colorScheme.secondary,
    label = "",
    iconRes = null,
    dismissible = ComposeDialogDismissible.Both,
)

data class ComposeDialogStyle(
    val containerColor: Color,
    val textColor: Color,
    val iconColor: Color,
    val label: String,
    @DrawableRes val iconRes: Int?,
    val dismissible: ComposeDialogDismissible,
)

enum class ComposeDialogDismissible {
    None, OnBackPress, OnClickOutside, Both;

    fun isDismissibleOnBackPress() = this in setOf(
        OnBackPress, Both,
    )

    fun isDismissibleOnClickOutside() = this in setOf(
        OnClickOutside, Both,
    )
}
