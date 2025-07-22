package com.pointlessapps.granite.home.ui.components.menu.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.granite.R
import com.pointlessapps.granite.ui.components.ComposeText
import com.pointlessapps.granite.ui.components.defaultComposeTextStyle
import com.pointlessapps.granite.ui.R as RC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DailyNoteButtonBottomSheet(
    state: SheetState,
    onHideButtonClicked: () -> Unit,
    onDisableClicked: () -> Unit,
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
                text = stringResource(R.string.daily_note),
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colorScheme.onSurface,
                    typography = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                ),
            )
            Spacer(Modifier.height(dimensionResource(RC.dimen.margin_tiny)))

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            ItemPropertiesButton(
                iconRes = RC.drawable.ic_visibility_off,
                label = R.string.hide_the_button,
                color = MaterialTheme.colorScheme.onSurface,
                onClick = {
                    onHideButtonClicked()
                    onDismissRequest()
                },
            )

            ItemPropertiesButton(
                iconRes = RC.drawable.ic_close,
                label = R.string.disable,
                description = R.string.disable_daily_note_description,
                color = colorResource(R.color.red),
                onClick = {
                    onDisableClicked()
                    onDismissRequest()
                },
            )
        }
    }
}
