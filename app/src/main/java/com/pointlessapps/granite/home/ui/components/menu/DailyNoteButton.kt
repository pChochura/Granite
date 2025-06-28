package com.pointlessapps.granite.home.ui.components.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.granite.R
import com.pointlessapps.granite.ui.components.ComposeIcon
import com.pointlessapps.granite.ui.components.ComposeText
import com.pointlessapps.granite.ui.components.defaultComposeIconStyle
import com.pointlessapps.granite.ui.components.defaultComposeTextStyle
import java.util.Date
import com.pointlessapps.granite.ui.R as RC

@Composable
internal fun DailyNoteButton(
    exists: Boolean,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .padding(horizontal = dimensionResource(RC.dimen.margin_medium))
            .padding(bottom = dimensionResource(RC.dimen.margin_medium))
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(dimensionResource(RC.dimen.margin_medium)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ComposeIcon(
            modifier = Modifier.size(dimensionResource(R.dimen.logo_icon_size)),
            iconRes = RC.drawable.ic_today,
            iconStyle = defaultComposeIconStyle().copy(
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
            ),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ComposeText(
                text = stringResource(R.string.todays_daily_note),
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    typography = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                ),
            )
            ComposeText(
                text = stringResource(R.string.todays_daily_note_description, Date()),
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                    typography = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                ),
            )
        }

        ComposeIcon(
            modifier = Modifier.size(dimensionResource(R.dimen.logo_icon_size)),
            iconRes = if (exists) RC.drawable.ic_arrow_right else RC.drawable.ic_plus,
            iconStyle = defaultComposeIconStyle().copy(
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
            ),
        )
    }
}
