package com.pointlessapps.granite.home.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.pointlessapps.granite.R
import com.pointlessapps.granite.home.model.Item
import com.pointlessapps.granite.home.ui.HomeViewModel
import com.pointlessapps.granite.home.ui.menu.dialog.CreateFolderDialog
import com.pointlessapps.granite.home.ui.menu.dialog.CreateFolderDialogData
import com.pointlessapps.granite.home.ui.menu.dialog.MoveDialog
import com.pointlessapps.granite.home.ui.menu.dialog.MoveDialogData
import com.pointlessapps.granite.home.ui.menu.dialog.OrderTypeDialog
import com.pointlessapps.granite.home.ui.menu.dialog.RenameDialog
import com.pointlessapps.granite.home.ui.menu.dialog.RenameDialogData
import kotlinx.coroutines.launch
import com.pointlessapps.granite.ui.R as RC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LeftSideMenu(
    viewModel: HomeViewModel,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    var createFolderDialogData by remember { mutableStateOf<CreateFolderDialogData?>(null) }
    var renameDialogData by remember { mutableStateOf<RenameDialogData?>(null) }
    var moveDialogData by remember { mutableStateOf<MoveDialogData?>(null) }
    var showOrderTypeDialog by remember { mutableStateOf(false) }
    var itemPropertiesBottomSheetData by remember { mutableStateOf<Item?>(null) }
    val itemPropertiesBottomSheetState = rememberModalBottomSheetState()

    val untitledText = stringResource(R.string.untitled)
    Column {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimensionResource(RC.dimen.margin_medium))
                .padding(top = dimensionResource(RC.dimen.margin_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(RC.dimen.margin_medium)),
        ) {
            TitleBar()
            SearchBar(
                searchValue = viewModel.state.searchValue,
                onSearchValueChanged = viewModel::onSearchChanged,
            )
            ItemTree(
                items = viewModel.state.filteredItems,
                deletedItems = viewModel.state.filteredDeletedItems,
                searchValue = viewModel.state.searchValue,
                selectedItemId = viewModel.state.openedItemId,
                openedFolderIds = viewModel.state.openedFolderIds,
                onItemSelected = viewModel::onItemSelected,
                onItemLongClick = {
                    itemPropertiesBottomSheetData = it
                    coroutineScope.launch {
                        itemPropertiesBottomSheetState.show()
                    }
                },
            )
        }

        BottomBar(
            isFolded = viewModel.state.openedFolderIds.isEmpty(),
            onAddFileClicked = viewModel::onAddFileClicked,
            onAddFolderClicked = {
                createFolderDialogData = CreateFolderDialogData(
                    name = TextFieldValue(
                        text = untitledText,
                        selection = TextRange(0, untitledText.length),
                    ),
                    parentId = null,
                )
                keyboardController?.show()
            },
            onSortClicked = { showOrderTypeDialog = true },
            onFoldClicked = viewModel::onFoldClicked,
        )
    }

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
                        folders = viewModel.state.foldersWithParents,
                    )

                    ItemPropertyAction.DUPLICATE -> viewModel.duplicateItem(item.id)
                    ItemPropertyAction.SHARE -> {}
                    ItemPropertyAction.RENAME -> renameDialogData = RenameDialogData(
                        name = TextFieldValue(
                            text = item.name,
                            selection = TextRange(0, item.name.length),
                        ),
                        id = item.id,
                    )

                    ItemPropertyAction.RESTORE -> viewModel.restoreItem(item.id)
                    ItemPropertyAction.DELETE -> viewModel.deleteItem(item.id)
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
            data = data,
            onInputChanged = { moveDialogData = moveDialogData?.copy(query = it) },
            onItemClicked = {
                viewModel.moveItem(data.itemId, it.id)
                moveDialogData = null
            },
            onDismissRequest = { moveDialogData = null },
        )
    }
}
