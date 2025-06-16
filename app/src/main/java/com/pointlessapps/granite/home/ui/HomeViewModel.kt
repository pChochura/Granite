package com.pointlessapps.granite.home.ui

import android.os.Parcelable
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.granite.domain.note.model.Note
import com.pointlessapps.granite.domain.note.usecase.CreateItemUseCase
import com.pointlessapps.granite.domain.note.usecase.DeleteItemsUseCase
import com.pointlessapps.granite.domain.note.usecase.DuplicateItemsUseCase
import com.pointlessapps.granite.domain.note.usecase.GetNotesUseCase
import com.pointlessapps.granite.domain.note.usecase.MarkItemsAsDeletedUseCase
import com.pointlessapps.granite.domain.note.usecase.UpdateItemUseCase
import com.pointlessapps.granite.home.mapper.toItem
import com.pointlessapps.granite.home.model.Item
import com.pointlessapps.granite.home.model.ItemOrderType
import com.pointlessapps.granite.home.utils.childrenOf
import com.pointlessapps.granite.home.utils.toSortedTree
import com.pointlessapps.granite.utils.TextFieldValueParceler
import com.pointlessapps.granite.utils.mutableStateOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.take
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith

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
) : Parcelable {

    fun List<Item>.filtered() = filter {
        val matchesSearch = it.name.contains(searchValue, ignoreCase = true) ||
                it.content?.contains(searchValue, ignoreCase = true) == true

        val isParentOpened = it.parentId !in this.map(Item::id) ||
                it.parentId in openedFolderIds ||
                it.parentId == null

        return@filter (searchValue.isNotBlank() && matchesSearch) || (searchValue.isBlank() && isParentOpened)
    }

    @IgnoredOnParcel
    val filteredItems = items.filtered()

    @IgnoredOnParcel
    val filteredDeletedItems = deletedItems.filtered()
}

internal sealed interface HomeEvent {
    data object CloseDrawer : HomeEvent
}

internal class HomeViewModel(
    savedStateHandle: SavedStateHandle,
    getNotesUseCase: GetNotesUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    private val createItemUseCase: CreateItemUseCase,
    private val markItemsAsDeletedUseCase: MarkItemsAsDeletedUseCase,
    private val duplicateItemsUseCase: DuplicateItemsUseCase,
    private val deleteItemsUseCase: DeleteItemsUseCase,
    private val untitledNotePlaceholder: String,
) : ViewModel() {

    private val eventChannel = Channel<HomeEvent>()
    val events = eventChannel.receiveAsFlow()

    var state by savedStateHandle.mutableStateOf(HomeState())
        private set

    init {
        getNotesUseCase()
            .take(1)
            .onStart { state = state.copy(isLoading = true) }
            .onEach {
                val (deletedItems, items) = it.map(Note::toItem).partition(Item::deleted)
                state = state.copy(
                    isLoading = false,
                    items = items.toSortedTree(state.orderType.comparator),
                    deletedItems = deletedItems.toSortedTree(state.orderType.comparator),
                )
            }
            .catch {
                state = state.copy(isLoading = false)
                it.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    fun onNoteContentChanged(value: TextFieldValue) {
        state = state.copy(noteContent = value)
    }

    fun onNoteTitleChanged(value: TextFieldValue) {
        state = state.copy(noteTitle = value)
    }

    fun onItemSelected(item: Item) {
        if (!item.isFolder) {
            eventChannel.trySend(HomeEvent.CloseDrawer)
            state = state.copy(
                openedItemId = item.id,
                noteTitle = TextFieldValue(text = item.name),
                noteContent = TextFieldValue(text = item.content.orEmpty()),
            )

            return
        }

        if (state.openedFolderIds.contains(item.id)) {
            closeNestedFolder(item)
        } else {
            state = state.copy(openedFolderIds = state.openedFolderIds + item.id)
        }
    }

    private fun closeNestedFolder(item: Item) {
        val items = if (item.deleted) state.deletedItems else state.items
        val innerFoldersIds = items.childrenOf(item)
            .mapNotNull { if (it.isFolder) it.id else null }

        state = state.copy(openedFolderIds = state.openedFolderIds - innerFoldersIds - item.id)
    }

    fun onAddFileClicked(parentId: Int? = null) {
        createNote(parentId)
        eventChannel.trySend(HomeEvent.CloseDrawer)
    }

    fun onOrderTypeSelected(orderType: ItemOrderType) {
        // TODO save the choice to disk
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
                state.items
                    .mapNotNull { if (it.isFolder) it.id else null }
                    .toSet()
            },
        )
    }

    fun onSearchChanged(value: String) {
        state = state.copy(searchValue = value)
    }

    fun saveNote(silently: Boolean = false) {
        updateItemUseCase(
            id = state.openedItemId ?: return,
            name = state.noteTitle.text.ifBlank { untitledNotePlaceholder },
            content = state.noteContent.text,
            parentId = state.items.find { it.id == state.openedItemId }?.parentId,
        )
            .take(1)
            .onStart { if (!silently) state = state.copy(isLoading = true) }
            .onEach { note ->
                state = state.copy(
                    isLoading = false,
                    openedItemId = note.id,
                    noteTitle = state.noteTitle.copy(text = note.name),
                    noteContent = state.noteContent.copy(text = note.content.orEmpty()),
                    items = state.items.map {
                        if (it.id == note.id) {
                            note.toItem().copy(indent = it.indent)
                        } else {
                            it
                        }
                    },
                )
            }
            .catch {
                state = state.copy(isLoading = false)
                it.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    private fun createNote(parentId: Int?) {
        createItemUseCase(
            name = untitledNotePlaceholder,
            content = "",
            parentId = parentId,
        )
            .take(1)
            .onStart { state = state.copy(isLoading = true) }
            .onEach { note ->
                state = state.copy(
                    isLoading = false,
                    openedItemId = note.id,
                    noteTitle = TextFieldValue(
                        text = note.name,
                        selection = TextRange(0, note.name.length),
                    ),
                    noteContent = TextFieldValue(text = note.content.orEmpty()),
                    items = (state.items + note.toItem()).toSortedTree(state.orderType.comparator),
                )
            }
            .catch {
                state = state.copy(isLoading = false)
                it.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    fun createFolder(name: String, parentId: Int?) {
        createItemUseCase(
            name = name,
            content = null,
            parentId = parentId,
        )
            .take(1)
            .onStart { state = state.copy(isLoading = true) }
            .onEach { folder ->
                state = state.copy(
                    isLoading = false,
                    items = (state.items + folder.toItem()).toSortedTree(state.orderType.comparator),
                )
            }
            .catch {
                state = state.copy(isLoading = false)
                it.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    fun renameItem(id: Int, name: String) {
        val item = state.items.find { it.id == id }
        updateItemUseCase(
            id = id,
            name = name,
            content = item?.content,
            parentId = item?.parentId,
        )
            .take(1)
            .onStart { state = state.copy(isLoading = true) }
            .onEach {
                state = state.copy(
                    isLoading = false,
                    items = state.items.toSortedTree(state.orderType.comparator),
                )

                if (id == state.openedItemId) {
                    state = state.copy(noteTitle = state.noteTitle.copy(text = name))
                }
            }
            .catch {
                state = state.copy(isLoading = false)
                it.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    fun deleteItem(id: Int) {
        val item = state.items.find { it.id == id } ?: return
        val items = listOf(item.copy(deleted = true, indent = 0)) +
                if (item.isFolder) {
                    state.items.childrenOf(item)
                        .map { it.copy(deleted = true, indent = it.indent - item.indent) }
                } else {
                    emptyList()
                }
        val ids = items.map(Item::id)

        markItemsAsDeletedUseCase(ids, deleted = true)
            .take(1)
            .onStart { state = state.copy(isLoading = true) }
            .onEach {
                closeNestedFolder(item)
                state = state.copy(
                    isLoading = false,
                    items = state.items.filter { it.id !in ids },
                    deletedItems = (state.deletedItems + items)
                        .toSortedTree(state.orderType.comparator),
                )

                if (state.openedItemId in ids) {
                    state = state.copy(openedItemId = null)
                }
            }
            .catch {
                state = state.copy(isLoading = false)
                it.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    fun deleteItemPermanently(id: Int) {
        val item = state.deletedItems.find { it.id == id } ?: return
        val ids = if (item.isFolder) {
            state.items.childrenOf(item).map(Item::id) + id
        } else listOf(id)

        deleteItemsUseCase(ids)
            .take(1)
            .onStart { state = state.copy(isLoading = true) }
            .onEach {
                closeNestedFolder(item)
                state = state.copy(
                    isLoading = false,
                    deletedItems = state.deletedItems.filter { it.id !in ids },
                )

                if (state.openedItemId in ids) {
                    state = state.copy(openedItemId = null)
                }
            }
            .catch {
                state = state.copy(isLoading = false)
                it.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    fun restoreItem(id: Int) {
        val item = state.deletedItems.find { it.id == id } ?: return
        val items = listOf(item.copy(deleted = false)) + if (item.isFolder) {
            state.deletedItems.childrenOf(item).map { it.copy(deleted = false) }
        } else emptyList()
        val ids = items.map(Item::id)

        markItemsAsDeletedUseCase(ids, deleted = false)
            .take(1)
            .onStart { state = state.copy(isLoading = true) }
            .onEach {
                state = state.copy(
                    isLoading = false,
                    items = (state.items + items).toSortedTree(state.orderType.comparator),
                    deletedItems = state.deletedItems.filter { it.id !in ids },
                )
            }
            .catch {
                state = state.copy(isLoading = false)
                it.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    fun duplicateItem(id: Int) {
        val item = state.items.find { it.id == id } ?: return
        val ids = listOf(id) + if (item.isFolder) {
            state.items.childrenOf(item).map(Item::id)
        } else {
            emptyList()
        }

        duplicateItemsUseCase(ids)
            .take(1)
            .onStart { state = state.copy(isLoading = true) }
            .onEach { notes ->
                state = state.copy(
                    isLoading = false,
                    items = (state.items + notes.map { it.toItem() })
                        .toSortedTree(state.orderType.comparator),
                )
            }
            .catch {
                state = state.copy(isLoading = false)
                it.printStackTrace()
            }
            .launchIn(viewModelScope)
    }
}
