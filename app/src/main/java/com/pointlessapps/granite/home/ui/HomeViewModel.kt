package com.pointlessapps.granite.home.ui

import android.app.Application
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.pointlessapps.granite.R
import com.pointlessapps.granite.domain.model.Note
import com.pointlessapps.granite.domain.note.usecase.CreateItemUseCase
import com.pointlessapps.granite.domain.note.usecase.DeleteItemsUseCase
import com.pointlessapps.granite.domain.note.usecase.DuplicateItemsUseCase
import com.pointlessapps.granite.domain.note.usecase.GetNotesUseCase
import com.pointlessapps.granite.domain.note.usecase.MarkItemsAsDeletedUseCase
import com.pointlessapps.granite.domain.note.usecase.MoveItemUseCase
import com.pointlessapps.granite.domain.note.usecase.UpdateItemUseCase
import com.pointlessapps.granite.domain.prefs.usecase.CreateDailyNotesFolderUseCase
import com.pointlessapps.granite.domain.prefs.usecase.GetDailyNotesEnabledUseCase
import com.pointlessapps.granite.domain.prefs.usecase.GetDailyNotesFolderUseCase
import com.pointlessapps.granite.domain.prefs.usecase.GetItemsOrderTypeUseCase
import com.pointlessapps.granite.domain.prefs.usecase.GetLastOpenedFileUseCase
import com.pointlessapps.granite.domain.prefs.usecase.SetItemsOrderTypeUseCase
import com.pointlessapps.granite.domain.prefs.usecase.SetLastOpenedFileUseCase
import com.pointlessapps.granite.fuzzy.search.FuzzySearch
import com.pointlessapps.granite.home.mapper.fromItemOrderType
import com.pointlessapps.granite.home.mapper.toItem
import com.pointlessapps.granite.home.mapper.toItemOrderType
import com.pointlessapps.granite.home.mapper.toItemWithParents
import com.pointlessapps.granite.home.model.Item
import com.pointlessapps.granite.home.model.ItemOrderType
import com.pointlessapps.granite.home.model.ItemWithParents
import com.pointlessapps.granite.home.ui.components.menu.dialog.ConfirmationDialogData
import com.pointlessapps.granite.home.utils.parentsOf
import com.pointlessapps.granite.home.utils.toSortedTree
import com.pointlessapps.granite.home.utils.withChildrenOf
import com.pointlessapps.granite.utils.TextFieldValueParceler
import com.pointlessapps.granite.utils.launch
import com.pointlessapps.granite.utils.mutableStateOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
internal data class HomeState(
    val noteTitle: @WriteWith<TextFieldValueParceler> TextFieldValue = TextFieldValue(),
    val noteContent: @WriteWith<TextFieldValueParceler> TextFieldValue = TextFieldValue(),
    val openedFolderIds: Set<Int> = emptySet(),
    val openedItemId: Int? = null,
    val orderType: ItemOrderType = ItemOrderType.NameAscending,
    val searchValue: String = "",
    val items: List<Item> = emptyList(),
    val deletedItems: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val todayDailyNoteExists: Boolean = false,
    val dailyNotesEnabled: Boolean = true,

    @IgnoredOnParcel
    val highlightedItem: Item? = null,
) : Parcelable {

    @IgnoredOnParcel
    val filteredItems = items.filtered()

    @IgnoredOnParcel
    val filteredDeletedItems = deletedItems.filtered()

    private fun List<Item>.filtered() = filter {
        val matchesSearch = FuzzySearch.extractWords(searchValue, it.name) != null ||
                it.content?.contains(searchValue, ignoreCase = true) == true

        val isParentOpened = it.parentId !in this.map(Item::id) ||
                it.parentId in openedFolderIds ||
                it.parentId == null

        return@filter (searchValue.isNotBlank() && matchesSearch) || (searchValue.isBlank() && isParentOpened)
    }

    fun getFoldersWithParentsExcept(id: Int): List<ItemWithParents> {
        // TODO remove the parent folder as well
        val itemsById = items.associateBy(Item::id)
        val result = mutableListOf(ItemWithParents(null, "/", ""))
        items.filter(Item::isFolder).filter { it.id != id }.forEach { folder ->
            val parents = mutableListOf<Item>()
            var currentParentId = folder.parentId
            while (currentParentId != null && currentParentId != id) {
                val parentItem = itemsById[currentParentId]
                if (parentItem != null) {
                    parents.add(parentItem)
                    currentParentId = parentItem.parentId
                } else {
                    break
                }
            }
            if (currentParentId == id) {
                return@forEach
            }

            result.add(folder.toItemWithParents(parents.reversed()))
        }

        return result.toList()
    }
}

internal sealed interface HomeEvent {
    data object CloseDrawer : HomeEvent
    data class ShowSnackbar(@StringRes val message: Int) : HomeEvent
    data class ShowConfirmationDialog(val data: ConfirmationDialogData) : HomeEvent
}

internal class HomeViewModel(
    savedStateHandle: SavedStateHandle,
    application: Application,
    getNotesUseCase: GetNotesUseCase,
    getLastOpenedFileUseCase: GetLastOpenedFileUseCase,
    getItemsOrderTypeUseCase: GetItemsOrderTypeUseCase,
    getDailyNotesEnabledUseCase: GetDailyNotesEnabledUseCase,
) : AndroidViewModel(application), KoinComponent {

    private val untitledNotePlaceholder = application.getString(R.string.untitled)

    private val getDailyNotesFolderUseCase: GetDailyNotesFolderUseCase by inject()
    private val createDailyNotesFolderUseCase: CreateDailyNotesFolderUseCase by inject()
    private val setLastOpenedFileUseCase: SetLastOpenedFileUseCase by inject()
    private val setItemsOrderTypeUseCase: SetItemsOrderTypeUseCase by inject()
    private val updateItemUseCase: UpdateItemUseCase by inject()
    private val createItemUseCase: CreateItemUseCase by inject()
    private val duplicateItemsUseCase: DuplicateItemsUseCase by inject()
    private val moveItemUseCase: MoveItemUseCase by inject()
    private val markItemsAsDeletedUseCase: MarkItemsAsDeletedUseCase by inject()
    private val deleteItemsUseCase: DeleteItemsUseCase by inject()

    private val eventChannel = Channel<HomeEvent>()
    val events = eventChannel.receiveAsFlow()

    var state by savedStateHandle.mutableStateOf(HomeState())
        private set

    private var openedFilesStack by savedStateHandle.mutableStateOf(emptyList<Int>())

    init {
        launch(
            onException = {
                it.printStackTrace()
                state = state.copy(isLoading = false)
                eventChannel.trySend(HomeEvent.ShowSnackbar(R.string.error_loading_notes))
            },
        ) {
            state = state.copy(isLoading = true)

            val (deletedItems, items) = getNotesUseCase().map(Note::toItem).partition(Item::deleted)
            val orderType = getItemsOrderTypeUseCase().toItemOrderType()
            val lastOpenedFile = getLastOpenedFileUseCase()
            state = state.copy(
                isLoading = false,
                items = items.toSortedTree(orderType.comparator),
                deletedItems = deletedItems.toSortedTree(orderType.comparator),
                openedItemId = lastOpenedFile?.id,
                noteTitle = TextFieldValue(text = lastOpenedFile?.name.orEmpty()),
                noteContent = TextFieldValue(text = lastOpenedFile?.content.orEmpty()),
                orderType = orderType,
                dailyNotesEnabled = getDailyNotesEnabledUseCase(),
            )
        }
    }

    fun peekFileFromStack(): Item? {
        val id = openedFilesStack.lastOrNull() ?: return null
        return state.items.find { it.id == id } ?: state.deletedItems.find { it.id == id }
    }

    fun openFileFromStack() {
        val item = peekFileFromStack() ?: return
        launch { setLastOpenedFileUseCase(item.id) }
        openedFilesStack = openedFilesStack.dropLast(1)
        state = state.copy(
            openedItemId = item.id,
            noteTitle = TextFieldValue(text = item.name),
            noteContent = TextFieldValue(text = item.content.orEmpty()),
        )
    }

    fun onNoteContentChanged(value: TextFieldValue) {
        state = state.copy(noteContent = value)
    }

    fun onNoteTitleChanged(value: TextFieldValue) {
        state = state.copy(noteTitle = value)
    }

    fun onSearchChanged(value: String) {
        state = state.copy(searchValue = value)
    }

    fun onItemSelected(item: Item) {
        if (state.searchValue.isNotBlank()) {
            state = state.copy(
                searchValue = "",
                highlightedItem = item,
                openedFolderIds = state.openedFolderIds + state.items.parentsOf(item).map { it.id },
            )

            viewModelScope.launch {
                delay(500)
                state = state.copy(highlightedItem = null)
            }

            return
        }

        if (!item.isFolder) {
            eventChannel.trySend(HomeEvent.CloseDrawer)

            if (state.openedItemId == item.id) return

            launch { setLastOpenedFileUseCase(item.id) }
            state.openedItemId?.also { openedFilesStack = openedFilesStack + it }
            state = state.copy(
                openedItemId = item.id,
                noteTitle = TextFieldValue(text = item.name),
                noteContent = TextFieldValue(text = item.content.orEmpty()),
            )

            return
        }

        if (state.openedFolderIds.contains(item.id)) {
            val items = if (item.deleted) state.deletedItems else state.items

            state = state.copy(
                openedFolderIds = state.openedFolderIds - items.withChildrenOf(item).map(Item::id),
            )
        } else {
            state = state.copy(openedFolderIds = state.openedFolderIds + item.id)
        }
    }

    fun onAddFileClicked(parentId: Int? = null) {
        launch(
            onException = {
                it.printStackTrace()
                state = state.copy(isLoading = false)
                eventChannel.trySend(HomeEvent.ShowSnackbar(R.string.error_creating_note))
            },
        ) {
            state = state.copy(isLoading = true)
            val items = createItemUseCase(untitledNotePlaceholder, "", parentId)
            val note = items.last()
            state = state.copy(
                isLoading = false,
                openedItemId = note.id,
                noteTitle = TextFieldValue(
                    text = note.name,
                    selection = TextRange(0, note.name.length),
                ),
                noteContent = TextFieldValue(text = note.content.orEmpty()),
                items = (state.items + items.map(Note::toItem))
                    .toSortedTree(state.orderType.comparator),
            )

            setLastOpenedFileUseCase(note.id)

            eventChannel.trySend(HomeEvent.CloseDrawer)
        }
    }

    fun onOrderTypeSelected(orderType: ItemOrderType) {
        launch { setItemsOrderTypeUseCase(orderType.fromItemOrderType()) }
        state = state.copy(
            orderType = orderType,
            items = state.items.toSortedTree(orderType.comparator),
        )
    }

    fun onFoldClicked() {
        state = state.copy(
            openedFolderIds = if (state.openedFolderIds.isNotEmpty()) {
                emptySet()
            } else {
                (state.items + state.deletedItems)
                    .mapNotNull { if (it.isFolder) it.id else null }
                    .toSet()
            },
        )
    }

    fun onDailyNoteClicked() {
        if (!state.todayDailyNoteExists) {
            launch(
                onException = {
                    it.printStackTrace()
                    state = state.copy(isLoading = false)
                    eventChannel.trySend(HomeEvent.ShowSnackbar(R.string.error_creating_note))
                }
            ) {
                state = state.copy(isLoading = true)

                val item =
                    (getDailyNotesFolderUseCase() ?: createDailyNotesFolderUseCase()).toItem()

                if (item.deleted) {
                    state = state.copy(isLoading = false)

                    return@launch eventChannel.send(
                        HomeEvent.ShowConfirmationDialog(
                            data = ConfirmationDialogData(
                                title = R.string.restore_folder,
                                description = R.string.restore_daily_notes_folder_description,
                                confirmText = R.string.restore,
                                cancelText = R.string.create_new,
                                onConfirmClicked = { restoreDailyNotesFolder(item.id) },
                                onCancelClicked = ::createDailyNotesFolder,
                            ),
                        ),
                    )
                }

                // If the folder was created add it to the tree
                if (state.items.find { it.id == item.id } == null) {
                    state = state.copy(
                        items = (state.items + item).toSortedTree(state.orderType.comparator),
                    )
                }

                // Make sure it is opened
                state = state.copy(openedFolderIds = state.openedFolderIds + item.id)

                val items = createItemUseCase(
                    // TODO store this as preferences
                    name = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date()),
                    content = "",
                    parentId = item.id,
                )
                val note = items.last()
                state = state.copy(
                    isLoading = false,
                    openedItemId = note.id,
                    noteTitle = TextFieldValue(
                        text = note.name,
                        selection = TextRange(0, note.name.length),
                    ),
                    noteContent = TextFieldValue(text = note.content.orEmpty()),
                    items = (state.items + items.map(Note::toItem))
                        .toSortedTree(state.orderType.comparator),
                )

                setLastOpenedFileUseCase(note.id)

                eventChannel.send(HomeEvent.CloseDrawer)
            }
        }
    }

    private fun restoreDailyNotesFolder(id: Int) {
        launch(
            onException = {
                it.printStackTrace()
                state = state.copy(isLoading = false)
                eventChannel.trySend(HomeEvent.ShowSnackbar(R.string.error_restoring_item))
            },
        ) {
            val items = state.deletedItems.withChildrenOf(id)
                ?.map { it.copy(deleted = false) } ?: return@launch
            val ids = items.map(Item::id)

            state = state.copy(isLoading = true)
            markItemsAsDeletedUseCase(ids, deleted = false)
            state = state.copy(
                isLoading = false,
                items = (state.items + items).toSortedTree(state.orderType.comparator),
                deletedItems = state.deletedItems.filter { it.id !in ids },
                openedFolderIds = state.openedFolderIds - items.map { it.id },
            )

            onDailyNoteClicked()
        }
    }

    private fun createDailyNotesFolder() {
        launch(
            onException = {
                it.printStackTrace()
                state = state.copy(isLoading = false)
                eventChannel.trySend(HomeEvent.ShowSnackbar(R.string.error_creating_folder))
            },
        ) {
            state = state.copy(isLoading = true)
            createDailyNotesFolderUseCase()
            state = state.copy(isLoading = false)

            onDailyNoteClicked()
        }
    }

    fun saveNote(silently: Boolean = false) {
        launch(
            onException = {
                it.printStackTrace()
                state = state.copy(isLoading = false)
                eventChannel.trySend(HomeEvent.ShowSnackbar(R.string.error_saving_note))
            },
        ) {
            val openedItemId = state.openedItemId ?: return@launch

            if (!silently) state = state.copy(isLoading = true)
            val note = updateItemUseCase(
                id = openedItemId,
                name = state.noteTitle.text.ifBlank { untitledNotePlaceholder },
                content = state.noteContent.text,
                parentId = state.items.find { it.id == openedItemId }?.parentId,
            )
            state = state.copy(
                isLoading = false,
                items = state.items.map {
                    if (it.id == note.id) {
                        note.toItem().copy(indent = it.indent)
                    } else {
                        it
                    }
                },
            )
        }
    }

    fun createFolder(name: String, parentId: Int?) {
        launch(
            onException = {
                it.printStackTrace()
                state = state.copy(isLoading = false)
                eventChannel.trySend(HomeEvent.ShowSnackbar(R.string.error_creating_folder))
            },
        ) {
            val folders = createItemUseCase(name = name, content = null, parentId = parentId)
            state = state.copy(
                isLoading = false,
                items = (state.items + folders.map(Note::toItem))
                    .toSortedTree(state.orderType.comparator),
            )
        }
    }

    fun renameItem(id: Int, name: String) {
        launch(
            onException = {
                it.printStackTrace()
                state = state.copy(isLoading = false)
                eventChannel.trySend(HomeEvent.ShowSnackbar(R.string.error_renaming_item))
            },
        ) {
            state = state.copy(isLoading = true)
            val item = state.items.find { it.id == id }
            updateItemUseCase(
                id = id,
                name = name,
                content = item?.content,
                parentId = item?.parentId,
            )
            state = state.copy(
                isLoading = false,
                items = state.items.map {
                    if (it.id == id) {
                        it.copy(name = name)
                    } else {
                        it
                    }
                }.toSortedTree(state.orderType.comparator),
                noteTitle = if (state.openedItemId == id) {
                    state.noteTitle.copy(text = name)
                } else {
                    state.noteTitle
                },
            )
        }
    }

    fun duplicateItem(id: Int) {
        launch(
            onException = {
                it.printStackTrace()
                state = state.copy(isLoading = false)
                eventChannel.trySend(HomeEvent.ShowSnackbar(R.string.error_duplicating_item))
            },
        ) {
            val ids = state.items.withChildrenOf(id)?.map(Item::id) ?: return@launch

            state = state.copy(isLoading = true)
            val notes = duplicateItemsUseCase(ids)
            state = state.copy(
                isLoading = false,
                items = (state.items + notes.map(Note::toItem))
                    .toSortedTree(state.orderType.comparator),
            )
        }
    }

    fun deleteItem(id: Int) {
        launch(
            onException = {
                it.printStackTrace()
                state = state.copy(isLoading = false)
                eventChannel.trySend(HomeEvent.ShowSnackbar(R.string.error_deleting_item))
            },
        ) {
            val items = state.items.withChildrenOf(id)
                ?.map { it.copy(deleted = true) } ?: return@launch
            val ids = items.map(Item::id)

            state = state.copy(isLoading = true)
            markItemsAsDeletedUseCase(ids, deleted = true)
            val newItems = (state.deletedItems + items).toSortedTree(state.orderType.comparator)
            state = state.copy(
                isLoading = false,
                items = state.items.filter { it.id !in ids },
                deletedItems = newItems,
                openedItemId = if (state.openedItemId in ids) null else state.openedItemId,
                openedFolderIds = state.openedFolderIds - (newItems.withChildrenOf(id)
                    ?.map(Item::id) ?: emptySet()),
            )
        }
    }

    fun deleteItemPermanently(id: Int) {
        launch(
            onException = {
                it.printStackTrace()
                state = state.copy(isLoading = false)
                eventChannel.trySend(HomeEvent.ShowSnackbar(R.string.error_deleting_item))
            },
        ) {
            val ids = state.deletedItems.withChildrenOf(id)?.map(Item::id) ?: return@launch

            state = state.copy(isLoading = true)
            deleteItemsUseCase(ids)
            state = state.copy(
                isLoading = false,
                deletedItems = state.deletedItems.filter { it.id !in ids },
                openedItemId = if (state.openedItemId in ids) null else state.openedItemId,
                openedFolderIds = state.openedFolderIds - ids
            )
        }
    }

    fun restoreItem(id: Int) {
        launch(
            onException = {
                it.printStackTrace()
                state = state.copy(isLoading = false)
                eventChannel.trySend(HomeEvent.ShowSnackbar(R.string.error_restoring_item))
            },
        ) {
            val items = state.deletedItems.withChildrenOf(id)
                ?.map { it.copy(deleted = false) } ?: return@launch
            val ids = items.map(Item::id)

            state = state.copy(isLoading = true)
            markItemsAsDeletedUseCase(ids, deleted = false)
            val newItems = (state.items + items).toSortedTree(state.orderType.comparator)
            state = state.copy(
                isLoading = false,
                items = newItems,
                deletedItems = state.deletedItems.filter { it.id !in ids },
                openedFolderIds = state.openedFolderIds - (newItems.withChildrenOf(id)
                    ?.map(Item::id) ?: emptySet()),
            )
        }
    }

    fun moveItem(id: Int, newParentId: Int?) {
        launch(
            onException = {
                it.printStackTrace()
                state = state.copy(isLoading = false)
                eventChannel.trySend(HomeEvent.ShowSnackbar(R.string.error_moving_item))
            }
        ) {
            state = state.copy(isLoading = true)
            moveItemUseCase(id, newParentId)
            state = state.copy(
                isLoading = false,
                items = state.items.map {
                    if (it.id == id) {
                        it.copy(parentId = newParentId)
                    } else {
                        it
                    }
                }.toSortedTree(state.orderType.comparator),
            )
        }
    }
}
