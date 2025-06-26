package com.pointlessapps.granite.home.ui

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.granite.R
import com.pointlessapps.granite.domain.model.Note
import com.pointlessapps.granite.domain.note.usecase.GetNotesUseCase
import com.pointlessapps.granite.domain.note.usecase.MoveItemUseCase
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
import com.pointlessapps.granite.home.ui.delegates.ItemCreationDelegate
import com.pointlessapps.granite.home.ui.delegates.ItemDeletionDelegate
import com.pointlessapps.granite.home.utils.childrenOf
import com.pointlessapps.granite.home.utils.parentsOf
import com.pointlessapps.granite.home.utils.toSortedTree
import com.pointlessapps.granite.utils.TextFieldValueParceler
import com.pointlessapps.granite.utils.mutableStateOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
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
    getNotesUseCase: GetNotesUseCase,
    getLastOpenedFileUseCase: GetLastOpenedFileUseCase,
    getItemsOrderTypeUseCase: GetItemsOrderTypeUseCase,
    getDailyNotesEnabledUseCase: GetDailyNotesEnabledUseCase,
) : ViewModel(), KoinComponent {

    private val getDailyNotesFolderUseCase: GetDailyNotesFolderUseCase by inject()
    private val createDailyNotesFolderUseCase: CreateDailyNotesFolderUseCase by inject()
    private val setLastOpenedFileUseCase: SetLastOpenedFileUseCase by inject()
    private val setItemsOrderTypeUseCase: SetItemsOrderTypeUseCase by inject()
    private val moveItemUseCase: MoveItemUseCase by inject()

    private val eventChannel = Channel<HomeEvent>()
    val events = eventChannel.receiveAsFlow()

    var state by savedStateHandle.mutableStateOf(HomeState())
        private set

    private val itemCreationDelegate: ItemCreationDelegate by inject { parametersOf(savedStateHandle) }
    private val itemDeletionDelegate: ItemDeletionDelegate by inject()

    private var openedFilesStack: List<Int>
        get() = itemCreationDelegate.openedFilesStack
        set(value) {
            itemCreationDelegate.openedFilesStack = value
        }

    init {
        combine(
            getLastOpenedFileUseCase(),
            getItemsOrderTypeUseCase(),
            getDailyNotesEnabledUseCase(),
            getNotesUseCase(),
        ) { lastOpenedFile, itemsOrderType, dailyNotesEnabled, notes ->
            val (deletedItems, items) = notes.map(Note::toItem).partition(Item::deleted)
            val orderType = itemsOrderType.toItemOrderType()
            state = state.copy(
                isLoading = false,
                items = items.toSortedTree(orderType.comparator),
                deletedItems = deletedItems.toSortedTree(orderType.comparator),
                openedItemId = lastOpenedFile?.id,
                noteTitle = TextFieldValue(text = lastOpenedFile?.name.orEmpty()),
                noteContent = TextFieldValue(text = lastOpenedFile?.content.orEmpty()),
                orderType = orderType,
                dailyNotesEnabled = dailyNotesEnabled,
            )
        }.take(1)
            .onStart { state = state.copy(isLoading = true) }
            .catch {
                it.printStackTrace()
                state = state.copy(isLoading = false)
                eventChannel.send(HomeEvent.ShowSnackbar(R.string.error_loading_notes))
            }
            .launchIn(viewModelScope)
    }

    fun peekFileFromStack(): Item? {
        val id = openedFilesStack.lastOrNull() ?: return null
        return state.items.find { it.id == id } ?: state.deletedItems.find { it.id == id }
    }

    fun openFileFromStack() {
        val item = peekFileFromStack() ?: return
        setLastOpenedFileUseCase(item.id).launchIn(viewModelScope)
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

            setLastOpenedFileUseCase(item.id).launchIn(viewModelScope)
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
            val innerFoldersIds = items.childrenOf(item)
                .mapNotNull { if (it.isFolder) it.id else null }

            state = state.copy(openedFolderIds = state.openedFolderIds - innerFoldersIds - item.id)
        } else {
            state = state.copy(openedFolderIds = state.openedFolderIds + item.id)
        }
    }

    fun onAddFileClicked(parentId: Int? = null) {
        itemCreationDelegate.createNote(
            state = state,
            setState = { state = it },
            eventChannel = eventChannel,
            parentId = parentId,
        ).onEach { eventChannel.trySend(HomeEvent.CloseDrawer) }
            .launchIn(viewModelScope)
    }

    fun onOrderTypeSelected(orderType: ItemOrderType) {
        setItemsOrderTypeUseCase(orderType.fromItemOrderType()).launchIn(viewModelScope)
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun onDailyNoteClicked() {
        if (!state.todayDailyNoteExists) {
            getDailyNotesFolderUseCase()
                .take(1)
                .onStart { state = state.copy(isLoading = true) }
                .filter { item ->
                    if (item?.deleted != false) {
                        if (item == null) {
                            createDailyNotesFolder()

                            return@filter false
                        }

                        eventChannel.send(
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

                        return@filter false
                    }

                    true
                }
                .flatMapMerge { item ->
                    val item = requireNotNull(item).toItem()
                    if (state.items.find { it.id == item.id } == null) {
                        state = state.copy(
                            items = (state.items + item).toSortedTree(state.orderType.comparator),
                        )
                    }

                    state = state.copy(openedFolderIds = state.openedFolderIds + item.id)

                    itemCreationDelegate.createNote(
                        state = state,
                        setState = { state = it },
                        eventChannel = eventChannel,
                        // TODO store this as preferences
                        name = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date()),
                        parentId = item.id,
                    )
                }
                .onEach { eventChannel.send(HomeEvent.CloseDrawer) }
                .catch {
                    it.printStackTrace()
                    state = state.copy(isLoading = false)
                    eventChannel.send(HomeEvent.ShowSnackbar(R.string.error_creating_note))
                }
                .launchIn(viewModelScope)
        }
    }

    private fun restoreDailyNotesFolder(id: Int) {
        itemDeletionDelegate.restoreItem(
            state = state,
            setState = { state = it },
            eventChannel = eventChannel,
            id = id,
        )
            ?.onEach { onDailyNoteClicked() }
            ?.launchIn(viewModelScope)
    }

    private fun createDailyNotesFolder() {
        createDailyNotesFolderUseCase()
            .take(1)
            .onStart { state = state.copy(isLoading = true) }
            .onEach { onDailyNoteClicked() }
            .catch {
                it.printStackTrace()
                state = state.copy(isLoading = false)
                eventChannel.send(HomeEvent.ShowSnackbar(R.string.error_creating_folder))
            }
            .launchIn(viewModelScope)
    }

    fun saveNote(silently: Boolean = false) {
        itemCreationDelegate.saveNote(
            state = state,
            setState = { state = it },
            eventChannel = eventChannel,
            silently = silently,
        )?.launchIn(viewModelScope)
    }

    fun createFolder(name: String, parentId: Int?) {
        itemCreationDelegate.createFolder(
            state = state,
            setState = { state = it },
            eventChannel = eventChannel,
            name = name,
            parentId = parentId,
        ).launchIn(viewModelScope)
    }

    fun renameItem(id: Int, name: String) {
        itemCreationDelegate.renameItem(
            state = state,
            setState = { state = it },
            eventChannel = eventChannel,
            id = id,
            name = name,
        ).launchIn(viewModelScope)
    }

    fun duplicateItem(id: Int) {
        itemCreationDelegate.duplicateItem(
            state = state,
            setState = { state = it },
            eventChannel = eventChannel,
            id = id,
        )?.launchIn(viewModelScope)
    }

    fun deleteItem(id: Int) {
        itemDeletionDelegate.deleteItem(
            state = state,
            setState = { state = it },
            eventChannel = eventChannel,
            id = id,
        )?.launchIn(viewModelScope)
    }

    fun deleteItemPermanently(id: Int) {
        itemDeletionDelegate.deleteItemPermanently(
            state = state,
            setState = { state = it },
            eventChannel = eventChannel,
            id = id,
        )?.launchIn(viewModelScope)
    }

    fun restoreItem(id: Int) {
        itemDeletionDelegate.restoreItem(
            state = state,
            setState = { state = it },
            eventChannel = eventChannel,
            id = id,
        )?.launchIn(viewModelScope)
    }

    fun moveItem(id: Int, newParentId: Int?) {
        moveItemUseCase(id, newParentId)
            .take(1)
            .onStart { state = state.copy(isLoading = true) }
            .onEach {
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
            .catch {
                it.printStackTrace()
                state = state.copy(isLoading = false)
                eventChannel.send(HomeEvent.ShowSnackbar(R.string.error_moving_item))
            }
            .launchIn(viewModelScope)
    }
}
