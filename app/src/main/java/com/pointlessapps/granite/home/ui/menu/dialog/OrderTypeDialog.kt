package com.pointlessapps.granite.home.ui.menu.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.pointlessapps.granite.R
import com.pointlessapps.granite.home.model.ItemOrderType
import com.pointlessapps.granite.ui_components.components.ComposeDialog
import com.pointlessapps.granite.ui_components.components.ComposeDialogDismissible
import com.pointlessapps.granite.ui_components.components.ComposeText
import com.pointlessapps.granite.ui_components.components.defaultComposeDialogStyle
import com.pointlessapps.granite.ui_components.components.defaultComposeTextStyle
import com.pointlessapps.granite.ui_components.R as RC

@Composable
internal fun OrderTypeDialog(
    onOrderTypeSelected: (ItemOrderType) -> Unit,
    onDismissRequest: () -> Unit,
) {
    ComposeDialog(
        onDismissRequest = onDismissRequest,
        dialogStyle = defaultComposeDialogStyle().copy(
            label = stringResource(R.string.item_ordering),
            iconRes = RC.drawable.ic_sort,
            dismissible = ComposeDialogDismissible.Both,
        ),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ItemOrderType.entries.forEach { entry ->
                ComposeText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        .clickable { onOrderTypeSelected(entry) }
                        .padding(
                            vertical = dimensionResource(RC.dimen.margin_tiny),
                            horizontal = dimensionResource(RC.dimen.margin_medium),
                        ),
                    text = stringResource(entry.label),
                    textStyle = defaultComposeTextStyle().copy(
                        textColor = MaterialTheme.colorScheme.onSurface,
                        typography = MaterialTheme.typography.labelLarge,
                    ),
                )
            }
        }
    }
}
