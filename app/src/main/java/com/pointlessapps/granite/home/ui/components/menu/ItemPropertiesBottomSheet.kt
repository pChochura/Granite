package com.pointlessapps.granite.home.ui.components.menu

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.pointlessapps.granite.R
import com.pointlessapps.granite.home.model.Item
import com.pointlessapps.granite.ui.components.ComposeIcon
import com.pointlessapps.granite.ui.components.ComposeText
import com.pointlessapps.granite.ui.components.defaultComposeIconStyle
import com.pointlessapps.granite.ui.components.defaultComposeTextStyle
import com.pointlessapps.granite.ui.R as RC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ItemPropertiesBottomSheet(
    state: SheetState,
    item: Item,
    onPropertyClicked: (ItemPropertyAction) -> Unit,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = state,
    ) {
        Column(
            modifier = Modifier.padding(
                vertical = dimensionResource(RC.dimen.margin_medium),
                horizontal = dimensionResource(RC.dimen.margin_big),
            ),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_tiny)),
        ) {
            ComposeText(
                modifier = Modifier.fillMaxWidth(),
                text = item.name,
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colorScheme.onSurface,
                    typography = MaterialTheme.typography.titleLarge,
                ),
            )
            Column(
                modifier = Modifier.padding(vertical = dimensionResource(RC.dimen.margin_tiny)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
            ) {
                ComposeText(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.created_at, item.createdAt),
                    textStyle = defaultComposeTextStyle().copy(
                        textColor = MaterialTheme.colorScheme.outline,
                        typography = MaterialTheme.typography.titleSmall,
                    ),
                )
                ComposeText(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.updated_at, item.updatedAt),
                    textStyle = defaultComposeTextStyle().copy(
                        textColor = MaterialTheme.colorScheme.outline,
                        typography = MaterialTheme.typography.titleSmall,
                    ),
                )
            }

            HorizontalDivider()

            if (item.deleted) {
                ItemPropertiesButton(
                    iconRes = RC.drawable.ic_restore,
                    label = R.string.restore,
                    color = MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        onPropertyClicked(ItemPropertyAction.RESTORE)
                        onDismissRequest()
                    },
                )
                ItemPropertiesButton(
                    iconRes = RC.drawable.ic_delete_permanently,
                    label = R.string.delete_permanently,
                    description = R.string.delete_item_permanently_description,
                    color = colorResource(R.color.red),
                    onClick = {
                        onPropertyClicked(ItemPropertyAction.DELETE_PERMANENTLY)
                        onDismissRequest()
                    },
                )
            } else {
                if (item.isFolder) {
                    ItemPropertiesButton(
                        iconRes = RC.drawable.ic_add_file,
                        label = R.string.create_note,
                        color = MaterialTheme.colorScheme.onSurface,
                        onClick = {
                            onPropertyClicked(ItemPropertyAction.ADD_FILE)
                            onDismissRequest()
                        },
                    )
                    ItemPropertiesButton(
                        iconRes = RC.drawable.ic_add_folder,
                        label = R.string.create_folder,
                        color = MaterialTheme.colorScheme.onSurface,
                        onClick = {
                            onPropertyClicked(ItemPropertyAction.ADD_FOLDER)
                            onDismissRequest()
                        },
                    )
                    HorizontalDivider()
                }

                ItemPropertiesButton(
                    iconRes = RC.drawable.ic_move_handle,
                    label = if (item.isFolder) R.string.move_folder else R.string.move_file,
                    color = MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        onPropertyClicked(ItemPropertyAction.MOVE)
                        onDismissRequest()
                    },
                )
                ItemPropertiesButton(
                    iconRes = RC.drawable.ic_duplicate,
                    label = R.string.duplicate,
                    color = MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        onPropertyClicked(ItemPropertyAction.DUPLICATE)
                        onDismissRequest()
                    },
                )
                HorizontalDivider()
                ItemPropertiesButton(
                    iconRes = RC.drawable.ic_share,
                    label = R.string.share,
                    color = MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        onPropertyClicked(ItemPropertyAction.SHARE)
                        onDismissRequest()
                    },
                )
                HorizontalDivider()
                ItemPropertiesButton(
                    iconRes = RC.drawable.ic_edit,
                    label = R.string.rename,
                    color = MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        onPropertyClicked(ItemPropertyAction.RENAME)
                        onDismissRequest()
                    },
                )
                ItemPropertiesButton(
                    iconRes = RC.drawable.ic_delete,
                    label = R.string.delete,
                    description = R.string.delete_item_description,
                    color = colorResource(R.color.red),
                    onClick = {
                        onPropertyClicked(ItemPropertyAction.DELETE)
                        onDismissRequest()
                    },
                )
            }
        }
    }
}

@Composable
private fun ItemPropertiesButton(
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


internal enum class ItemPropertyAction {
    ADD_FILE, ADD_FOLDER, MOVE, DUPLICATE, SHARE, RENAME, RESTORE, DELETE, DELETE_PERMANENTLY
}