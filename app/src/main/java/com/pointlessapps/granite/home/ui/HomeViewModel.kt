package com.pointlessapps.granite.home.ui

import android.os.Parcelable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.granite.domain.note.usecase.GetNotesUseCase
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
    val textValue: @WriteWith<TextFieldValueParceler> TextFieldValue = TextFieldValue(),
    val items: List<Item> = listOf(
        Folder(
            id = 2,
            name = "Work Documents",
            updatedAt = "2023-10-27T11:00:00Z",
            createdAt = "2023-10-25T09:15:00Z",
            indent = 0,
            items = listOf(
                Folder(
                    id = 5,
                    name = "Archive",
                    updatedAt = "2023-10-27T12:00:00Z",
                    createdAt = "2023-10-24T10:00:00Z",
                    indent = 1,
                    items = listOf(
                        File(
                            id = 6,
                            name = "OldContract.pdf",
                            updatedAt = "2022-01-15T13:00:00Z",
                            createdAt = "2022-01-10T11:00:00Z",
                            content = "Details of an old contract.",
                            indent = 2,
                        )
                    ),
                    opened = false,
                ),
                File(
                    id = 3,
                    name = "Report.docx",
                    updatedAt = "2023-10-27T08:00:00Z",
                    createdAt = "2023-10-27T08:00:00Z",
                    content = "Monthly report content.",
                    indent = 1,
                ),
                File(
                    id = 4,
                    name = "Presentation.pptx",
                    updatedAt = "2023-10-26T15:00:00Z",
                    createdAt = "2023-10-26T15:00:00Z",
                    content = "Slides for the presentation.",
                    indent = 1,
                ),
            ),
            opened = false,
        ),
        Folder(
            id = 9,
            name = "Recipes",
            updatedAt = "2023-10-27T18:00:00Z",
            createdAt = "2023-10-20T17:00:00Z",
            indent = 0,
            items = listOf(
                File(
                    id = 10,
                    name = "PastaRecipe.txt",
                    updatedAt = "2023-10-21T19:00:00Z",
                    createdAt = "2023-10-21T19:00:00Z",
                    content = "Ingredients and steps for pasta.",
                    indent = 1,
                ),
            ),
            opened = false,
        ),
        File(
            id = 1,
            name = "Document1.txt",
            updatedAt = "2023-10-27T10:00:00Z",
            createdAt = "2023-10-26T14:30:00Z",
            content = "Content of Document1.",
            indent = 0,
        ),
        File(
            id = 7,
            name = "Photo1.jpg",
            updatedAt = "2023-10-26T16:45:00Z",
            createdAt = "2023-10-26T16:45:00Z",
            content = "Image data for Photo1",
            indent = 0,
        ),
        File(
            id = 8,
            name = "Notes.md",
            updatedAt = "2023-10-28T09:30:00Z",
            createdAt = "2023-10-28T09:30:00Z",
            content = "# Personal Notes\n- Remember to buy milk\n- Meeting at 3 PM",
            indent = 0,
        ),
        File(
            id = 11,
            name = "Instructions.txt",
            updatedAt = "2023-10-29T10:10:10Z",
            createdAt = "2023-10-29T10:10:10Z",
            content = "Step-by-step instructions.",
            indent = 0,
        )
    ),
    val selectedItemId: Int = 1,
) : Parcelable

internal sealed interface HomeEvent {
    data object CloseDrawer : HomeEvent
}

internal class HomeViewModel(
    savedStateHandle: SavedStateHandle,
    getNotesUseCase: GetNotesUseCase,
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
                            content = it.content,
                        )
                    }
                )
            }
            .catch { /* add error handling */ }
            .launchIn(viewModelScope)
    }

    fun onTextValueChanged(value: TextFieldValue) {
        state = state.copy(textValue = value)
    }

    fun onItemSelected(item: Item) {
        when (item) {
            is File -> {
                state = state.copy(
                    selectedItemId = item.id,
                    textValue = TextFieldValue(text = item.content),
                )
                eventChannel.trySend(HomeEvent.CloseDrawer)
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
}
