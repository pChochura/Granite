package com.pointlessapps.granite.home.ui.menu

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
import com.pointlessapps.granite.ui_components.components.ComposeIcon
import com.pointlessapps.granite.ui_components.components.ComposeText
import com.pointlessapps.granite.ui_components.components.defaultComposeIconStyle
import com.pointlessapps.granite.ui_components.components.defaultComposeTextStyle
import com.pointlessapps.granite.ui_components.R as RC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ItemPropertiesBottomSheet(
    state: SheetState,
    item: Item,
    onAddFileClicked: () -> Unit,
    onAddFolderClicked: () -> Unit,
    onMoveClicked: () -> Unit,
    onRenameClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
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

            if (item.isFolder) {
                ItemPropertiesButton(
                    iconRes = RC.drawable.ic_add_file,
                    label = R.string.create_note,
                    color = MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        onAddFileClicked()
                        onDismissRequest()
                    },
                )
                ItemPropertiesButton(
                    iconRes = RC.drawable.ic_add_folder,
                    label = R.string.create_folder,
                    color = MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        onAddFolderClicked()
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
                    onMoveClicked()
                    onDismissRequest()
                },
            )
            HorizontalDivider()
            ItemPropertiesButton(
                iconRes = RC.drawable.ic_edit,
                label = R.string.rename,
                color = MaterialTheme.colorScheme.onSurface,
                onClick = {
                    onRenameClicked()
                    onDismissRequest()
                },
            )
            HorizontalDivider()
            ItemPropertiesButton(
                iconRes = RC.drawable.ic_delete,
                label = R.string.delete,
                color = colorResource(R.color.red),
                onClick = {
                    onDeleteClicked()
                    onDismissRequest()
                },
            )
        }
    }
}

@Composable
private fun ItemPropertiesButton(
    @DrawableRes iconRes: Int,
    @StringRes label: Int,
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

        ComposeText(
            text = stringResource(label),
            textStyle = defaultComposeTextStyle().copy(
                textColor = color,
                typography = MaterialTheme.typography.labelLarge,
            ),
        )
    }
}
