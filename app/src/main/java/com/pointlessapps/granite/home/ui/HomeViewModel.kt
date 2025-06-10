package com.pointlessapps.granite.home.ui

import android.os.Parcelable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.granite.domain.note.usecase.GetNoteUseCase
import com.pointlessapps.granite.domain.note.usecase.GetNotesUseCase
import com.pointlessapps.granite.domain.note.usecase.UpsertNoteUseCase
import com.pointlessapps.granite.home.model.File
import com.pointlessapps.granite.home.model.Folder
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
            .onStart { /* add loading */ }
            .onEach {
                /* remove loading */
                state = state.copy(
                    items = it.fastMap {
                        File(
                            id = it.id,
                            name = it.name,
                            updatedAt = "",
                            createdAt = "",
                            indent = 1,
                            content = it.content.orEmpty(),
                        )
                    }
                )
            }
            .catch {
                /* add error handling */
                it.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    fun onTextValueChanged(value: TextFieldValue) {
        state = state.copy(noteContent = value)
    }

    fun onItemSelected(item: Item) {
        when (item) {
            is File -> {
                state = state.copy(selectedItemId = item.id)
                eventChannel.trySend(HomeEvent.CloseDrawer)

                getNoteUseCase(item.id)
                    .onStart { state = state.copy(isLoading = true) }
                    .onEach {
                        state = state.copy(isLoading = false)
                        state = state.copy(
                            noteContent = TextFieldValue(text = it?.content.orEmpty()),
                        )
                    }
                    .catch {
                        state = state.copy(isLoading = false)
                        it.printStackTrace()
                    }
                    .launchIn(viewModelScope)
            }

            is Folder -> state = state.copy(
                items = state.items.fastMap {
                    if (it.id == item.id) {
                        item.copy(opened = !item.opened)
                    } else {
                        it
                    }
                }
            )
        }
    }

    private fun Folder.flatten(): List<Item> = listOf(this) + items.fastFlatMap {
        if (it is Folder && it.opened) it.flatten() else listOf(it)
    }

    fun getFlattenItems() = state.items.fastFlatMap {
        if (it is Folder && it.opened) {
            it.flatten()
        } else listOf(it)
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
