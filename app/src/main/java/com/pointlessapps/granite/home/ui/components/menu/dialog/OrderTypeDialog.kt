package com.pointlessapps.granite.home.ui.components.menu.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.granite.R
import com.pointlessapps.granite.home.model.ItemOrderType
import com.pointlessapps.granite.ui.components.ComposeButton
import com.pointlessapps.granite.ui.components.ComposeDialog
import com.pointlessapps.granite.ui.components.ComposeDialogDismissible
import com.pointlessapps.granite.ui.components.ComposeIcon
import com.pointlessapps.granite.ui.components.ComposeText
import com.pointlessapps.granite.ui.components.defaultComposeButtonStyle
import com.pointlessapps.granite.ui.components.defaultComposeButtonTextStyle
import com.pointlessapps.granite.ui.components.defaultComposeDialogStyle
import com.pointlessapps.granite.ui.components.defaultComposeIconStyle
import com.pointlessapps.granite.ui.components.defaultComposeTextStyle
import com.pointlessapps.granite.utils.applyIf
import com.pointlessapps.granite.ui.R as RC

@Composable
internal fun OrderTypeDialog(
    selectedType: ItemOrderType,
    onOrderTypeSelected: (ItemOrderType) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var currentlySelectedType by remember { mutableStateOf(selectedType) }

    ComposeDialog(
        onDismissRequest = onDismissRequest,
        dialogStyle = defaultComposeDialogStyle().copy(
            label = stringResource(R.string.sort_items_by),
            iconRes = RC.drawable.ic_sort,
            dismissible = ComposeDialogDismissible.OnBackPress,
        ),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .border(
                    width = dimensionResource(RC.dimen.default_border_width),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = MaterialTheme.shapes.small,
                ),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            itemsIndexed(ItemOrderType.entries) { index, orderType ->
                Row(
                    modifier = Modifier
                        // TODO add proper animation
                        .animateItem()
                        .fillMaxWidth()
                        .applyIf(index % 2 == 0) {
                            background(MaterialTheme.colorScheme.surfaceContainer)
                        }
                        .clickable(
                            role = Role.Button,
                            onClick = { currentlySelectedType = orderType },
                        )
                        .padding(
                            vertical = dimensionResource(RC.dimen.margin_small),
                            horizontal = dimensionResource(RC.dimen.margin_medium),
                        ),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_tiny)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (currentlySelectedType == orderType) {
                        ComposeIcon(
                            modifier = Modifier.size(dimensionResource(RC.dimen.caption_icon_size)),
                            iconRes = RC.drawable.ic_done,
                            iconStyle = defaultComposeIconStyle().copy(tint = MaterialTheme.colorScheme.onSurface),
                        )
                    }

                    ComposeText(
                        modifier = Modifier.weight(1f),
                        text = stringResource(orderType.label),
                        textStyle = defaultComposeTextStyle().copy(
                            textColor = MaterialTheme.colorScheme.onSurface,
                            typography = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (currentlySelectedType == orderType) {
                                    FontWeight.Bold
                                } else {
                                    MaterialTheme.typography.labelMedium.fontWeight
                                },
                            ),
                        ),
                    )

                    ComposeIcon(
                        modifier = Modifier.size(dimensionResource(RC.dimen.caption_icon_size)),
                        iconRes = if (orderType.isAscending) {
                            RC.drawable.ic_ascending
                        } else {
                            RC.drawable.ic_descending
                        },
                        iconStyle = defaultComposeIconStyle().copy(tint = MaterialTheme.colorScheme.onSurface),
                    )
                }
            }
        }

        ComposeButton(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.save),
            onClick = {
                onOrderTypeSelected(currentlySelectedType)
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
    }
}
