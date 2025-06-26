package com.pointlessapps.granite.home.ui.delegates

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import com.pointlessapps.granite.R
import com.pointlessapps.granite.domain.model.Note
import com.pointlessapps.granite.domain.note.usecase.CreateItemUseCase
import com.pointlessapps.granite.domain.note.usecase.DuplicateItemsUseCase
import com.pointlessapps.granite.domain.note.usecase.UpdateItemUseCase
import com.pointlessapps.granite.domain.prefs.usecase.SetLastOpenedFileUseCase
import com.pointlessapps.granite.home.mapper.toItem
import com.pointlessapps.granite.home.model.Item
import com.pointlessapps.granite.home.ui.HomeEvent
import com.pointlessapps.granite.home.ui.HomeState
import com.pointlessapps.granite.home.utils.childrenOf
import com.pointlessapps.granite.home.utils.toSortedTree
import com.pointlessapps.granite.utils.mutableStateOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class ItemCreationDelegate(
    savedStateHandle: SavedStateHandle,
    private val untitledNotePlaceholder: String,
) : KoinComponent {

    private val updateItemUseCase: UpdateItemUseCase by inject()
    private val createItemUseCase: CreateItemUseCase by inject()
    private val duplicateItemsUseCase: DuplicateItemsUseCase by inject()
    private val setLastOpenedFileUseCase: SetLastOpenedFileUseCase by inject()

    var openedFilesStack by savedStateHandle.mutableStateOf(emptyList<Int>())

    fun saveNote(
        state: HomeState,
        setState: (HomeState) -> Unit,
        eventChannel: Channel<HomeEvent>,
        silently: Boolean,
    ): Flow<*>? {
        return updateItemUseCase(
            id = state.openedItemId ?: return null,
            name = state.noteTitle.text.ifBlank { untitledNotePlaceholder },
            content = state.noteContent.text,
            parentId = state.items.find { it.id == state.openedItemId }?.parentId,
        )
            .take(1)
            .onStart { if (!silently) setState(state.copy(isLoading = true)) }
            .onEach { note ->
                setState(
                    state.copy(
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
                    ),
                )
            }
            .catch {
                it.printStackTrace()
                setState(state.copy(isLoading = false))
                eventChannel.send(HomeEvent.ShowSnackbar(R.string.error_saving_note))
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun createNote(
        state: HomeState,
        setState: (HomeState) -> Unit,
        eventChannel: Channel<HomeEvent>,
        name: String = untitledNotePlaceholder,
        parentId: Int?,
    ): Flow<*> {
        state.openedItemId?.also { openedFilesStack = openedFilesStack + it }

        return createItemUseCase(
            name = name,
            content = "",
            parentId = parentId,
        )
            .take(1)
            .onStart { setState(state.copy(isLoading = true)) }
            .onEach { items ->
                // It has to contain at least one element
                // Otherwise throw an error
                val note = items.last()
                setState(
                    state.copy(
                        isLoading = false,
                        openedItemId = note.id,
                        noteTitle = TextFieldValue(
                            text = note.name,
                            selection = TextRange(0, note.name.length),
                        ),
                        noteContent = TextFieldValue(text = note.content.orEmpty()),
                        items = (state.items + items.map { it.toItem() })
                            .toSortedTree(state.orderType.comparator),
                    ),
                )
            }
            .flatMapMerge { setLastOpenedFileUseCase(it.last().id) }
            .catch {
                it.printStackTrace()
                setState(state.copy(isLoading = false))
                eventChannel.send(HomeEvent.ShowSnackbar(R.string.error_creating_note))
            }
    }

    fun createFolder(
        state: HomeState,
        setState: (HomeState) -> Unit,
        eventChannel: Channel<HomeEvent>,
        name: String,
        parentId: Int?,
    ): Flow<*> {
        return createItemUseCase(
            name = name,
            content = null,
            parentId = parentId,
        )
            .take(1)
            .onStart { setState(state.copy(isLoading = true)) }
            .onEach { folders ->
                setState(
                    state.copy(
                        isLoading = false,
                        items = (state.items + folders.map { it.toItem() })
                            .toSortedTree(state.orderType.comparator),
                    ),
                )
            }
            .catch {
                it.printStackTrace()
                setState(state.copy(isLoading = false))
                eventChannel.send(HomeEvent.ShowSnackbar(R.string.error_creating_folder))
            }
    }

    fun renameItem(
        state: HomeState,
        setState: (HomeState) -> Unit,
        eventChannel: Channel<HomeEvent>,
        id: Int,
        name: String,
    ): Flow<*> {
        val item = state.items.find { it.id == id }

        return updateItemUseCase(
            id = id,
            name = name,
            content = item?.content,
            parentId = item?.parentId,
        )
            .take(1)
            .onStart { setState(state.copy(isLoading = true)) }
            .onEach {
                setState(
                    state.copy(
                        isLoading = false,
                        items = state.items.map {
                            if (it.id == id) {
                                it.copy(name = name)
                            } else {
                                it
                            }
                        }.toSortedTree(state.orderType.comparator),
                        noteTitle = if (id == state.openedItemId) {
                            state.noteTitle.copy(text = name)
                        } else {
                            state.noteTitle
                        },
                    ),
                )
            }
            .catch {
                it.printStackTrace()
                setState(state.copy(isLoading = false))
                eventChannel.send(HomeEvent.ShowSnackbar(R.string.error_renaming_item))
            }
    }

    fun duplicateItem(
        state: HomeState,
        setState: (HomeState) -> Unit,
        eventChannel: Channel<HomeEvent>,
        id: Int,
    ): Flow<*>? {
        val item = state.items.find { it.id == id } ?: return null
        val ids = listOf(id) + if (item.isFolder) {
            state.items.childrenOf(item).map(Item::id)
        } else emptyList()

        return duplicateItemsUseCase(ids)
            .take(1)
            .onStart { setState(state.copy(isLoading = true)) }
            .onEach { notes ->
                setState(
                    state.copy(
                        isLoading = false,
                        items = (state.items + notes.map(Note::toItem))
                            .toSortedTree(state.orderType.comparator),
                    ),
                )
            }
            .catch {
                it.printStackTrace()
                setState(state.copy(isLoading = false))
                eventChannel.send(HomeEvent.ShowSnackbar(R.string.error_duplicating_item))
            }
    }
}
