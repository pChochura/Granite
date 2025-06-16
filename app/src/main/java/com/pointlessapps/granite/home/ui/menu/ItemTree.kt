package com.pointlessapps.granite.home.ui.menu

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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

@Composable
internal fun ColumnScope.ItemTree(
    items: List<Item>,
    deletedItems: List<Item>,
    selectedItemId: Int?,
    openedFolderIds: Set<Int>,
    onItemSelected: (Item) -> Unit,
    onItemLongClick: (Item) -> Unit,
) {
    var isTrashOpened by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
    ) {
        items(items, key = { it.id }) { item ->
            Item(
                item = item,
                isFileOpened = selectedItemId == item.id,
                isFolderOpened = openedFolderIds.contains(item.id),
                onItemSelected = onItemSelected,
                onItemLongClick = onItemLongClick,
            )
        }

        item {
            Column {
                HorizontalDivider(modifier = Modifier.animateItem())
                Row(
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .clickable(role = Role.Button, onClick = { isTrashOpened = !isTrashOpened })
                        .padding(
                            vertical = dimensionResource(RC.dimen.margin_tiny),
                            horizontal = dimensionResource(RC.dimen.margin_nano),
                        ),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val rotation by animateFloatAsState(if (isTrashOpened) 90f else 0f)
                    ComposeIcon(
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.folder_icon_size))
                            .rotate(rotation),
                        iconRes = RC.drawable.ic_arrow_right,
                        iconStyle = defaultComposeIconStyle().copy(
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                    ComposeText(
                        text = stringResource(R.string.deleted),
                        textStyle = defaultComposeTextStyle().copy(
                            textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            typography = MaterialTheme.typography.titleMedium,
                        ),
                    )
                }
            }
        }

        if (isTrashOpened) {
            items(deletedItems, key = { it.id }) { item ->
                Item(
                    item = item,
                    isFileOpened = selectedItemId == item.id,
                    isFolderOpened = openedFolderIds.contains(item.id),
                    onItemSelected = onItemSelected,
                    onItemLongClick = onItemLongClick,
                )
            }
        }
    }
}

@Composable
private fun LazyItemScope.Item(
    item: Item,
    isFileOpened: Boolean,
    isFolderOpened: Boolean,
    onItemSelected: (Item) -> Unit,
    onItemLongClick: (Item) -> Unit,
) {
    Row(
        modifier = Modifier
            .animateItem()
            .fillMaxWidth()
            .alpha(if (item.deleted) 0.5f else 1f)
            .clip(MaterialTheme.shapes.small)
            .then(
                if (isFileOpened) {
                    Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                } else {
                    Modifier
                },
            )
            .combinedClickable(
                role = Role.Button,
                onClick = { onItemSelected(item) },
                onLongClick = { onItemLongClick(item) },
            )
            .padding(
                vertical = dimensionResource(RC.dimen.margin_tiny),
                horizontal = dimensionResource(RC.dimen.margin_nano),
            )
            .padding(start = dimensionResource(RC.dimen.margin_medium).times(item.indent)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (item.isFolder) {
            val rotation by animateFloatAsState(if (isFolderOpened) 90f else 0f)
            ComposeIcon(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.folder_icon_size))
                    .rotate(rotation),
                iconRes = RC.drawable.ic_arrow_right,
                iconStyle = defaultComposeIconStyle().copy(
                    tint = if (isFileOpened) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                ),
            )
        }

        ComposeText(
            text = item.name,
            textStyle = defaultComposeTextStyle().copy(
                textColor = if (isFileOpened) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                typography = MaterialTheme.typography.bodySmall,
            ),
        )
    }
}
