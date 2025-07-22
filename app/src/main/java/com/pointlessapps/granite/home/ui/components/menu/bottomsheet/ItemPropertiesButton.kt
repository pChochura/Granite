package com.pointlessapps.granite.home.ui.components.menu.bottomsheet

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.pointlessapps.granite.ui.components.ComposeIcon
import com.pointlessapps.granite.ui.components.ComposeText
import com.pointlessapps.granite.ui.components.defaultComposeIconStyle
import com.pointlessapps.granite.ui.components.defaultComposeTextStyle
import com.pointlessapps.granite.ui.R as RC

@Composable
internal fun ItemPropertiesButton(
    @DrawableRes iconRes: Int,
    @StringRes label: Int,
    @StringRes description: Int? = null,
    color: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = dimensionResource(RC.dimen.margin_tiny)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_tiny)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ComposeIcon(
            iconRes = iconRes,
            iconStyle = defaultComposeIconStyle().copy(tint = color),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
        ) {
            ComposeText(
                text = stringResource(label),
                textStyle = defaultComposeTextStyle().copy(
                    textColor = color,
                    typography = MaterialTheme.typography.labelLarge,
                ),
            )

            if (description != null) {
                ComposeText(
                    text = stringResource(description),
                    textStyle = defaultComposeTextStyle().copy(
                        textColor = color.copy(0.6f),
                        typography = MaterialTheme.typography.labelSmall,
                    ),
                )
            }
        }
    }
}
