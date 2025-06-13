package com.pointlessapps.granite.home.ui

import android.os.Parcelable
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.granite.domain.note.usecase.CreateItemUseCase
import com.pointlessapps.granite.domain.note.usecase.GetNotesUseCase
import com.pointlessapps.granite.domain.note.usecase.UpdateItemUseCase
import com.pointlessapps.granite.home.mapper.insertSorted
import com.pointlessapps.granite.home.mapper.toItem
import com.pointlessapps.granite.home.mapper.toNote
import com.pointlessapps.granite.home.mapper.toNoteComparator
import com.pointlessapps.granite.home.mapper.toSortedItems
import com.pointlessapps.granite.home.model.Item
import com.pointlessapps.granite.home.model.ItemOrderType
import com.pointlessapps.granite.utils.TextFieldValueParceler
import com.pointlessapps.granite.utils.mutableStateOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
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
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false,
) : Parcelable {
    @IgnoredOnParcel
    val filteredItems = items.filter {
        it.parentId in openedFolderIds || it.parentId == null
    }
}

internal sealed interface HomeEvent {
    data object CloseDrawer : HomeEvent
    data object FocusOnTitle : HomeEvent
}

internal class HomeViewModel(
    savedStateHandle: SavedStateHandle,
    getNotesUseCase: GetNotesUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    private val createItemUseCase: CreateItemUseCase,
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
                state = state.copy(
                    isLoading = false,
                    items = it.toSortedItems(state.orderType.comparator.toNoteComparator()),
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
            val innerFoldersIds = state.items.drop(state.items.indexOf(item) + 1)
                .takeWhile { it.indent > item.indent }
                .mapNotNull { if (it.isFolder) it.id else null }

            state = state.copy(openedFolderIds = state.openedFolderIds - innerFoldersIds - item.id)
        } else {
            state = state.copy(openedFolderIds = state.openedFolderIds + item.id)
        }
    }

    fun onAddFileClicked() {
        createNote()
        viewModelScope.launch {
            eventChannel.send(HomeEvent.CloseDrawer)
            eventChannel.send(HomeEvent.FocusOnTitle)
        }
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
                            note.toItem(it.indent)
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

    fun createNote() {
        createItemUseCase(
            name = untitledNotePlaceholder,
            content = "",
            parentId = null,
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
                    items = state.items.insertSorted(note, state.orderType.comparator),
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
                    items = state.items.insertSorted(folder, state.orderType.comparator),
                )
            }
            .catch {
                state = state.copy(isLoading = false)
                it.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    fun onOrderTypeSelected(orderType: ItemOrderType) {
        // TODO save the choice to disk
        state = state.copy(
            orderType = orderType,
            items = state.items.map(Item::toNote)
                .toSortedItems(orderType.comparator.toNoteComparator()),
        )
    }
}
