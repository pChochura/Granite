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
import com.pointlessapps.granite.home.ui.delegates.ItemCreationDelegate
import com.pointlessapps.granite.home.ui.delegates.ItemDeletionDelegate
import com.pointlessapps.granite.home.utils.childrenOf
import com.pointlessapps.granite.home.utils.parentsOf
import com.pointlessapps.granite.home.utils.toSortedTree
import com.pointlessapps.granite.utils.TextFieldValueParceler
import com.pointlessapps.granite.utils.mutableStateOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
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
}

internal class HomeViewModel(
    savedStateHandle: SavedStateHandle,
    getNotesUseCase: GetNotesUseCase,
    getLastOpenedFileUseCase: GetLastOpenedFileUseCase,
    getItemsOrderTypeUseCase: GetItemsOrderTypeUseCase,
) : ViewModel(), KoinComponent {

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
            getNotesUseCase(),
        ) { lastOpenedFile, itemsOrderType, notes -> Triple(lastOpenedFile, itemsOrderType, notes) }
            .take(1)
            .onStart { state = state.copy(isLoading = true) }
            .onEach { (lastOpenedFile, itemsOrderType, notes) ->
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
                )
            }
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
        createNote(parentId)
        eventChannel.trySend(HomeEvent.CloseDrawer)
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

    fun onSearchChanged(value: String) {
        state = state.copy(searchValue = value)
    }

    fun saveNote(silently: Boolean = false) {
        itemCreationDelegate.saveNote(
            state = state,
            setState = { state = it },
            eventChannel = eventChannel,
            silently = silently,
        )?.launchIn(viewModelScope)
    }

    private fun createNote(parentId: Int?) {
        itemCreationDelegate.createNote(
            state = state,
            setState = { state = it },
            eventChannel = eventChannel,
            parentId = parentId,
        ).launchIn(viewModelScope)
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
