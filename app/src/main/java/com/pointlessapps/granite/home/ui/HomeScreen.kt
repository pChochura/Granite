package com.pointlessapps.granite.home.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.pointlessapps.granite.LocalBottomSectionState
import com.pointlessapps.granite.LocalSnackbarHostState
import com.pointlessapps.granite.R
import com.pointlessapps.granite.home.model.Item
import com.pointlessapps.granite.home.ui.components.menu.bottomsheet.ItemPropertiesBottomSheet
import com.pointlessapps.granite.home.ui.components.menu.bottomsheet.ItemPropertyAction
import com.pointlessapps.granite.home.ui.components.menu.dialog.ConfirmationDialog
import com.pointlessapps.granite.home.ui.components.menu.dialog.ConfirmationDialogData
import com.pointlessapps.granite.home.ui.components.menu.dialog.CreateFolderDialog
import com.pointlessapps.granite.home.ui.components.menu.dialog.CreateFolderDialogData
import com.pointlessapps.granite.home.ui.components.menu.dialog.MoveDialog
import com.pointlessapps.granite.home.ui.components.menu.dialog.MoveDialogData
import com.pointlessapps.granite.home.ui.components.menu.dialog.OrderTypeDialog
import com.pointlessapps.granite.home.ui.components.menu.dialog.RenameDialog
import com.pointlessapps.granite.home.ui.components.menu.dialog.RenameDialogData
import com.pointlessapps.granite.navigation.Route
import com.pointlessapps.granite.relative.datetime.formatter.RelativeDatetimeFormatter
import com.pointlessapps.granite.relative.datetime.formatter.RelativeDatetimeFormatter.Result
import com.pointlessapps.granite.ui.components.ComposeButton
import com.pointlessapps.granite.ui.components.ComposeIcon
import com.pointlessapps.granite.ui.components.ComposeLoader
import com.pointlessapps.granite.ui.components.ComposeScaffoldLayout
import com.pointlessapps.granite.ui.components.ComposeText
import com.pointlessapps.granite.ui.components.TopBar
import com.pointlessapps.granite.ui.components.defaultComposeButtonStyle
import com.pointlessapps.granite.ui.components.defaultComposeIconStyle
import com.pointlessapps.granite.ui.components.defaultComposeTextStyle
import com.pointlessapps.granite.utils.plus
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.Date
import com.pointlessapps.granite.ui.R as RC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateTo: (Route) -> Unit,
) {
    var createFolderDialogData by remember { mutableStateOf<CreateFolderDialogData?>(null) }
    var confirmationDialogData by remember { mutableStateOf<ConfirmationDialogData?>(null) }
    var renameDialogData by remember { mutableStateOf<RenameDialogData?>(null) }
    var moveDialogData by remember { mutableStateOf<MoveDialogData?>(null) }
    var showOrderTypeDialog by remember { mutableStateOf(false) }
    var itemPropertiesBottomSheetData by remember { mutableStateOf<Item?>(null) }
    val itemPropertiesBottomSheetState = rememberModalBottomSheetState()

    val localSnackbarHostState = LocalSnackbarHostState.current
    val coroutineScope = rememberCoroutineScope()

    LifecycleResumeEffect(Unit) {
        viewModel.loadItems()

        coroutineScope.launch {
            viewModel.events.collect {
                when (it) {
                    is HomeEvent.ShowSnackbar -> localSnackbarHostState.showSnackbar(it.message)
                    is HomeEvent.NavigateTo -> onNavigateTo(it.route)
                }
            }
        }

        onPauseOrDispose {}
    }

    ComposeScaffoldLayout(
        topBar = {
            TopBar(
                leftIcon = R.drawable.ic_logo,
                leftIconTooltip = R.string.app_name,
                rightIcon = RC.drawable.ic_settings,
                rightIconTooltip = R.string.settings,
                title = R.string.app_name,
                onLeftIconClicked = {},
                onRightIconClicked = {},
            )
        },
        content = { contentPadding ->
            var dailyNotesButtonHeight by remember { mutableIntStateOf(0) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow),
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = contentPadding.calculateTopPadding() +
                                    with(LocalDensity.current) { dailyNotesButtonHeight.toDp() } +
                                    dimensionResource(RC.dimen.large_rounded_corners),
                        )
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(top = contentPadding.calculateTopPadding())
                        .onSizeChanged { dailyNotesButtonHeight = it.height }
                        .padding(
                            vertical = dimensionResource(RC.dimen.margin_semi_big),
                            horizontal = dimensionResource(RC.dimen.margin_semi_big),
                        ),
                ) {
                    ComposeButton(
                        modifier = Modifier.fillMaxWidth(),
                        label = stringResource(R.string.daily_notes),
                        onClick = {},
                        buttonStyle = defaultComposeButtonStyle().copy(
                            iconRes = RC.drawable.ic_calendar,
                            containerColor = MaterialTheme.colorScheme.primary,
                            textStyle = defaultComposeTextStyle().copy(
                                textColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        )
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = contentPadding + LocalBottomSectionState.current.asPaddingValues + PaddingValues(
                        top = with(LocalDensity.current) { dailyNotesButtonHeight.toDp() }
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                ) {
                    item {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dimensionResource(RC.dimen.large_rounded_corners))
                                .clip(
                                    shape = RoundedCornerShape(
                                        topStart = dimensionResource(RC.dimen.large_rounded_corners),
                                        topEnd = dimensionResource(RC.dimen.large_rounded_corners)
                                    ),
                                )
                                .background(MaterialTheme.colorScheme.surfaceContainer),
                        )
                    }

                    items(viewModel.state.filteredItems, key = { it.id }) {
                        when {
                            it.isFolder -> FolderNameItem(
                                item = it,
                                isOpened = it.id in viewModel.state.openedFolderIds,
                                onItemClicked = { viewModel.onItemSelected(it) },
                                onItemLongClicked = {
                                    itemPropertiesBottomSheetData = it
                                    coroutineScope.launch {
                                        itemPropertiesBottomSheetState.show()
                                    }
                                },
                            )

                            else -> NoteItem(
                                item = it,
                                onItemClicked = { viewModel.onItemSelected(it) },
                                onItemLongClicked = {
                                    itemPropertiesBottomSheetData = it
                                    coroutineScope.launch {
                                        itemPropertiesBottomSheetState.show()
                                    }
                                },
                            )
                        }
                    }
                }
            }

        },
    )

    ComposeLoader(viewModel.state.isLoading)

    val untitledText = stringResource(R.string.untitled)
    itemPropertiesBottomSheetData?.let { item ->
        ItemPropertiesBottomSheet(
            state = itemPropertiesBottomSheetState,
            item = item,
            onPropertyClicked = {
                when (it) {
                    ItemPropertyAction.ADD_FILE -> viewModel.onAddFileClicked(item.id)
                    ItemPropertyAction.ADD_FOLDER ->
                        createFolderDialogData = CreateFolderDialogData(
                            name = TextFieldValue(
                                text = untitledText,
                                selection = TextRange(0, untitledText.length),
                            ),
                            parentId = item.id,
                        )

                    ItemPropertyAction.MOVE -> moveDialogData = MoveDialogData(
                        itemId = item.id,
                        folders = viewModel.state.getFoldersWithParentsExcept(item.id),
                    )

                    ItemPropertyAction.DUPLICATE -> viewModel.duplicateItem(item.id)
                    ItemPropertyAction.SHARE -> {
                        // TODO add share
                    }

                    ItemPropertyAction.RENAME -> renameDialogData = RenameDialogData(
                        name = TextFieldValue(
                            text = item.name,
                            selection = TextRange(0, item.name.length),
                        ),
                        id = item.id,
                    )

                    ItemPropertyAction.RESTORE -> viewModel.restoreItem(item.id)
                    ItemPropertyAction.DELETE -> {
                        confirmationDialogData = ConfirmationDialogData(
                            title = if (item.isFolder) {
                                R.string.delete_folder_question
                            } else {
                                R.string.delete_note_question
                            },
                            description = if (item.isFolder) {
                                R.string.delete_folder_question_description
                            } else {
                                R.string.delete_note_question_description
                            },
                            confirmText = R.string.delete,
                            cancelText = R.string.cancel,
                            isError = true,
                            onConfirmClicked = {
                                viewModel.deleteItem(item.id)
                                confirmationDialogData = null
                            },
                            onCancelClicked = { confirmationDialogData = null },
                        )
                    }

                    ItemPropertyAction.DELETE_PERMANENTLY -> viewModel.deleteItemPermanently(item.id)
                }
            },
            onDismissRequest = {
                coroutineScope.launch {
                    itemPropertiesBottomSheetState.hide()
                }.invokeOnCompletion {
                    itemPropertiesBottomSheetData = null
                }
            },
        )
    }

    if (showOrderTypeDialog) {
        OrderTypeDialog(
            selectedType = viewModel.state.orderType,
            onOrderTypeSelected = {
                viewModel.onOrderTypeSelected(it)
                showOrderTypeDialog = false
            },
            onDismissRequest = { showOrderTypeDialog = false },
        )
    }

    createFolderDialogData?.let { data ->
        CreateFolderDialog(
            data = data,
            onNameChanged = { createFolderDialogData = createFolderDialogData?.copy(name = it) },
            onSaveClicked = {
                viewModel.createFolder(data.name.text, data.parentId)
                createFolderDialogData = null
            },
            onDismissRequest = { createFolderDialogData = null },
        )
    }

    renameDialogData?.let { data ->
        RenameDialog(
            data = data,
            onNameChanged = { renameDialogData = renameDialogData?.copy(name = it) },
            onSaveClicked = {
                viewModel.renameItem(data.id, data.name.text)
                renameDialogData = null
            },
            onDismissRequest = { renameDialogData = null },
        )
    }

    moveDialogData?.let { data ->
        MoveDialog(
            dialogData = data,
            onInputChanged = { moveDialogData = moveDialogData?.copy(query = it) },
            onItemClicked = {
                viewModel.moveItem(data.itemId, it.id)
                moveDialogData = null
            },
            onCreateNewFolderClicked = {
                viewModel.moveItemToNewFolder(data.itemId, it.name)
                moveDialogData = null
            },
            onDismissRequest = { moveDialogData = null },
        )
    }

    confirmationDialogData?.let { data ->
        ConfirmationDialog(
            data = data,
            onDismissRequest = { confirmationDialogData = null },
        )
    }
}

@Composable
private fun LazyItemScope.FolderNameItem(
    item: Item,
    isOpened: Boolean,
    onItemClicked: () -> Unit,
    onItemLongClicked: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .animateItem()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            // FIXME
//            .padding(start = dimensionResource(RC.dimen.margin_medium).times(item.indent))
            .combinedClickable(
                role = Role.Button,
                onClick = onItemClicked,
                onLongClick = onItemLongClicked,
            ),
        shape = MaterialTheme.shapes.small,
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = dimensionResource(RC.dimen.margin_semi_big),
                vertical = dimensionResource(RC.dimen.margin_tiny),
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
        ) {
            ComposeText(
                modifier = Modifier.weight(1f),
                text = item.name,
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colorScheme.onBackground,
                    typography = MaterialTheme.typography.labelLarge,
                )
            )
            val rotation by animateFloatAsState(if (isOpened) 90f else 0f)
            ComposeIcon(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.folder_icon_size))
                    .rotate(rotation),
                iconRes = RC.drawable.ic_arrow_right,
                iconStyle = defaultComposeIconStyle().copy(
                    tint = MaterialTheme.colorScheme.onBackground,
                ),
            )
        }
    }
}

@Composable
private fun LazyItemScope.NoteItem(
    item: Item,
    onItemClicked: () -> Unit,
    onItemLongClicked: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .animateItem()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(vertical = dimensionResource(RC.dimen.margin_nano))
            .padding(horizontal = dimensionResource(RC.dimen.margin_semi_big))
            // FIXME
//            .padding(start = dimensionResource(RC.dimen.margin_medium).times(item.indent))
            .combinedClickable(
                role = Role.Button,
                onClick = onItemClicked,
                onLongClick = onItemLongClicked,
            ),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(
            width = dimensionResource(RC.dimen.default_border_width),
            color = MaterialTheme.colorScheme.outlineVariant,
        ),
        shape = MaterialTheme.shapes.small,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = dimensionResource(RC.dimen.margin_small),
                    vertical = dimensionResource(RC.dimen.margin_small),
                ),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_tiny)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
            ) {
                ComposeText(
                    modifier = Modifier.weight(1f),
                    text = item.name,
                    textStyle = defaultComposeTextStyle().copy(
                        textColor = MaterialTheme.colorScheme.onSurface,
                        typography = MaterialTheme.typography.labelLarge,
                    )
                )
                ComposeText(
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
                        textColor = MaterialTheme.colorScheme.onSurface,
                        typography = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.End,
                    )
                )
            }

            if (item.tags.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_nano)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    items(item.tags, key = { it.id }) { tag ->
                        val color = Color(tag.color)
                        ComposeText(
                            text = tag.name,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(color)
                                .padding(
                                    vertical = dimensionResource(RC.dimen.margin_nano),
                                    horizontal = dimensionResource(RC.dimen.margin_tiny),
                                ),
                            textStyle = defaultComposeTextStyle().copy(
                                textColor = if (color.luminance() > 0.5) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onPrimary
                                },
                                typography = MaterialTheme.typography.labelSmall,
                            ),
                        )
                    }
                }
            }
        }
    }
}
