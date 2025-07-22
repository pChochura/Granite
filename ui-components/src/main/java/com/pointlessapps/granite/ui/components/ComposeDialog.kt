package com.pointlessapps.granite.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pointlessapps.granite.ui.R
import kotlin.math.max

@Composable
fun ComposeDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dialogStyle: ComposeDialogStyle = defaultComposeDialogStyle(),
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = dialogStyle.dismissible.isDismissibleOnBackPress(),
            dismissOnClickOutside = dialogStyle.dismissible.isDismissibleOnClickOutside(),
        ),
    ) {
        Box {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .background(dialogStyle.containerColor)
                    .border(
                        width = dimensionResource(R.dimen.default_border_width),
                        color = dialogStyle.outlineColor,
                        shape = MaterialTheme.shapes.large,
                    )
                    .padding(horizontal = dimensionResource(R.dimen.margin_big))
                    .padding(
                        top = dimensionResource(R.dimen.margin_huge),
                        bottom = dimensionResource(R.dimen.margin_big),
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.margin_big),
                ),
            ) {
                ComposeText(
                    text = dialogStyle.label,
                    textStyle = defaultComposeTextStyle().copy(
                        typography = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        textColor = dialogStyle.textColor,
                    ),
                )

                content()
            }

            if (dialogStyle.iconRes != null) {
                var iconSize by remember { mutableIntStateOf(0) }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .onSizeChanged { iconSize = max(it.height, it.width) }
                        .offset { IntOffset(x = 0, y = -iconSize / 2) }
                        .background(
                            color = dialogStyle.accentColor,
                            shape = CircleShape,
                        )
                        .border(
                            width = dimensionResource(R.dimen.default_border_width),
                            color = dialogStyle.containerColor,
                            shape = CircleShape,
                        )
                        .padding(dimensionResource(R.dimen.margin_small)),
                    contentAlignment = Alignment.Center,
                ) {
                    ComposeIcon(
                        modifier = Modifier.size(dimensionResource(R.dimen.dialog_icon_size)),
                        iconRes = dialogStyle.iconRes,
                        iconStyle = defaultComposeIconStyle().copy(tint = dialogStyle.iconColor),
                    )
                }
            }
        }
    }
}

@Composable
fun defaultComposeDialogStyle() = ComposeDialogStyle(
    label = "",
    iconRes = null,
    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    outlineColor = MaterialTheme.colorScheme.outlineVariant,
    accentColor = MaterialTheme.colorScheme.primary,
    textColor = MaterialTheme.colorScheme.onSurface,
    iconColor = MaterialTheme.colorScheme.onPrimary,
    dismissible = ComposeDialogDismissible.Both,
)

data class ComposeDialogStyle(
    val label: String,
    @DrawableRes val iconRes: Int?,
    val containerColor: Color,
    val outlineColor: Color,
    val accentColor: Color,
    val textColor: Color,
    val iconColor: Color,
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
