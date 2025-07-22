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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.granite.R
import com.pointlessapps.granite.home.model.Item
import com.pointlessapps.granite.relative.datetime.formatter.RelativeDatetimeFormatter
import com.pointlessapps.granite.relative.datetime.formatter.RelativeDatetimeFormatter.Result
import com.pointlessapps.granite.ui.components.ComposeText
import com.pointlessapps.granite.ui.components.defaultComposeTextStyle
import java.util.Date
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ComposeText(
                    modifier = Modifier.fillMaxWidth(),
                    text = item.name,
                    textStyle = defaultComposeTextStyle().copy(
                        textColor = MaterialTheme.colorScheme.onSurface,
                        typography = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                    ),
                )
                ComposeText(
                    modifier = Modifier.fillMaxWidth(),
                    text = when (val result =
                        RelativeDatetimeFormatter.formatDateTime(item.updatedAt)) {
                        Result.LessThanMinuteAgo -> stringResource(R.string.date_less_than_minute_ago)
                        is Result.MinutesAgo -> stringResource(
                            R.string.date_minutes_ago,
                            result.minutes,
                        )

                        is Result.HoursAgo -> stringResource(R.string.date_hours_ago, result.hours)
                        Result.Yesterday -> stringResource(R.string.date_yesterday)
                        is Result.DaysAgo -> stringResource(R.string.date_days_ago, result.days)
                        Result.LastWeek -> stringResource(R.string.date_last_week)
                        is Result.Absolute -> stringResource(
                            R.string.date_absolute,
                            Date(result.time),
                        )
                    },
                    textStyle = defaultComposeTextStyle().copy(
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        typography = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                    ),
                )
            }
            Spacer(Modifier.height(dimensionResource(RC.dimen.margin_tiny)))

            // TODO add tags

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            ItemProperties(
                item = item,
                onPropertyClicked = onPropertyClicked,
                onDismissRequest = onDismissRequest,
            )
        }
    }
}

@Composable
private fun ItemProperties(
    item: Item,
    onPropertyClicked: (ItemPropertyAction) -> Unit,
    onDismissRequest: () -> Unit,
) {
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
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        }

        ItemPropertiesButton(
            iconRes = RC.drawable.ic_move,
            label = if (item.isFolder) R.string.move_folder else R.string.move_file,
            color = MaterialTheme.colorScheme.onSurface,
            onClick = {
                onPropertyClicked(ItemPropertyAction.MOVE)
                onDismissRequest()
            },
        )
        ItemPropertiesButton(
            iconRes = if (item.isFolder) RC.drawable.ic_duplicate_folder else RC.drawable.ic_duplicate_file,
            label = R.string.duplicate,
            color = MaterialTheme.colorScheme.onSurface,
            onClick = {
                onPropertyClicked(ItemPropertyAction.DUPLICATE)
                onDismissRequest()
            },
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ItemPropertiesButton(
            iconRes = RC.drawable.ic_share,
            label = R.string.share,
            color = MaterialTheme.colorScheme.onSurface,
            onClick = {
                onPropertyClicked(ItemPropertyAction.SHARE)
                onDismissRequest()
            },
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
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

internal enum class ItemPropertyAction {
    ADD_FILE, ADD_FOLDER, MOVE, DUPLICATE, SHARE, RENAME, RESTORE, DELETE, DELETE_PERMANENTLY
}
