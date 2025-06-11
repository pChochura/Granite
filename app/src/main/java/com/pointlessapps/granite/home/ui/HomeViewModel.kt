package com.pointlessapps.granite.home.ui

import android.os.Parcelable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.granite.domain.note.usecase.GetNoteUseCase
import com.pointlessapps.granite.domain.note.usecase.GetNotesUseCase
import com.pointlessapps.granite.domain.note.usecase.UpsertNoteUseCase
import com.pointlessapps.granite.home.mapper.toSortedItems
import com.pointlessapps.granite.home.model.Item
import com.pointlessapps.granite.utils.TextFieldValueParceler
import com.pointlessapps.granite.utils.mutableStateOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith

@Parcelize
internal data class HomeState(
    val noteTitle: String = "",
    val noteContent: @WriteWith<TextFieldValueParceler> TextFieldValue = TextFieldValue(),
    val items: List<Item> = emptyList(),
    val openedFolderIds: Set<Int> = emptySet(),
    val selectedItemId: Int = -1,
    val isLoading: Boolean = false,
) : Parcelable

internal sealed interface HomeEvent {
    data object CloseDrawer : HomeEvent
}

internal class HomeViewModel(
    savedStateHandle: SavedStateHandle,
    getNotesUseCase: GetNotesUseCase,
    private val getNoteUseCase: GetNoteUseCase,
    private val upsertNoteUseCase: UpsertNoteUseCase,
) : ViewModel() {

    private val eventChannel = Channel<HomeEvent>()
    val events = eventChannel.receiveAsFlow()

    var state by savedStateHandle.mutableStateOf(HomeState())
        private set

    init {
        getNotesUseCase()
            .onStart { state = state.copy(isLoading = true) }
            .onEach {
                state = state.copy(
                    isLoading = false,
                    items = it.toSortedItems(),
                )
            }
            .catch {
                state = state.copy(isLoading = false)
                it.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    fun getFilteredItems() = state.items.filter {
        it.parentId in state.openedFolderIds || it.parentId == null
    }

    fun onNoteContentChanged(value: TextFieldValue) {
        state = state.copy(noteContent = value)
    }

    fun onNoteTitleChanged(value: String) {
        state = state.copy(noteTitle = value)
    }

    fun onItemSelected(item: Item) {
        if (!item.isFolder) {
            state = state.copy(selectedItemId = item.id)
            eventChannel.trySend(HomeEvent.CloseDrawer)

            getNoteUseCase(item.id)
                .onStart { state = state.copy(isLoading = true) }
                .onEach {
                    state = state.copy(
                        isLoading = false,
                        noteTitle = it?.name.orEmpty(),
                        noteContent = TextFieldValue(text = it?.content.orEmpty()),
                    )
                }
                .catch {
                    state = state.copy(isLoading = false)
                    it.printStackTrace()
                }
                .launchIn(viewModelScope)
        } else {
            state = state.copy(
                openedFolderIds = if (state.openedFolderIds.contains(item.id)) {
                    state.openedFolderIds - state.items.drop(state.items.indexOf(item))
                        .takeWhile {
                            it.indent > item.indent
                        }.mapNotNull { if (it.isFolder) it.id else null }
                } else {
                    state.openedFolderIds + item.id
                },
            )
        }
    }

    fun saveNote() {
        upsertNoteUseCase(
            id = state.selectedItemId.takeIf { it != -1 },
            name = state.noteTitle,
            content = state.noteContent.text,
            parentId = null,
        )
            .onStart { state = state.copy(isLoading = true) }
            .onEach {
                state = state.copy(
                    isLoading = false,
                    selectedItemId = it,
                )
            }
            .catch {
                state = state.copy(isLoading = false)
                it.printStackTrace()
            }
            .launchIn(viewModelScope)
    }
}
