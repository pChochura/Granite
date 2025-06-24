package com.pointlessapps.granite.home.ui.delegates

import com.pointlessapps.granite.R
import com.pointlessapps.granite.domain.note.usecase.DeleteItemsUseCase
import com.pointlessapps.granite.domain.note.usecase.MarkItemsAsDeletedUseCase
import com.pointlessapps.granite.home.model.Item
import com.pointlessapps.granite.home.ui.HomeEvent
import com.pointlessapps.granite.home.ui.HomeState
import com.pointlessapps.granite.home.utils.childrenOf
import com.pointlessapps.granite.home.utils.toSortedTree
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take

internal class ItemDeletionDelegate(
    private val markItemsAsDeletedUseCase: MarkItemsAsDeletedUseCase,
    private val deleteItemsUseCase: DeleteItemsUseCase,
) {
    private fun closeNestedFolder(state: HomeState, item: Item): HomeState {
        val items = if (item.deleted) state.deletedItems else state.items
        val innerFoldersIds = items.childrenOf(item)
            .mapNotNull { if (it.isFolder) it.id else null }

        return state.copy(openedFolderIds = state.openedFolderIds - innerFoldersIds - item.id)
    }

    fun deleteItem(
        state: HomeState,
        setState: (HomeState) -> Unit,
        eventChannel: Channel<HomeEvent>,
        id: Int,
    ): Flow<*>? {
        val item = state.items.find { it.id == id } ?: return null
        val items = listOf(item.copy(deleted = true)) + if (item.isFolder) {
            state.items.childrenOf(item).map { it.copy(deleted = true) }
        } else emptyList()
        val ids = items.map(Item::id)

        return markItemsAsDeletedUseCase(ids, deleted = true)
            .take(1)
            .onStart { setState(state.copy(isLoading = true)) }
            .onEach {
                val newState = closeNestedFolder(state, item)
                setState(
                    newState.copy(
                        isLoading = false,
                        items = newState.items.filter { it.id !in ids },
                        deletedItems = (newState.deletedItems + items)
                            .toSortedTree(newState.orderType.comparator),
                        openedItemId = if (newState.openedItemId in ids) null else newState.openedItemId,
                    ),
                )
            }
            .catch {
                it.printStackTrace()
                setState(state.copy(isLoading = false))
                eventChannel.send(HomeEvent.ShowSnackbar(R.string.error_deleting_item))
            }
    }

    fun deleteItemPermanently(
        state: HomeState,
        setState: (HomeState) -> Unit,
        eventChannel: Channel<HomeEvent>,
        id: Int,
    ): Flow<*>? {
        val item = state.deletedItems.find { it.id == id } ?: return null
        val ids = if (item.isFolder) {
            state.deletedItems.childrenOf(item).map(Item::id) + id
        } else listOf(id)

        return deleteItemsUseCase(ids)
            .take(1)
            .onStart { setState(state.copy(isLoading = true)) }
            .onEach {
                val newState = closeNestedFolder(state, item)
                setState(
                    newState.copy(
                        isLoading = false,
                        deletedItems = newState.deletedItems.filter { it.id !in ids },
                        openedItemId = if (newState.openedItemId in ids) null else newState.openedItemId,
                    ),
                )
            }
            .catch {
                it.printStackTrace()
                setState(state.copy(isLoading = false))
                eventChannel.send(HomeEvent.ShowSnackbar(R.string.error_deleting_item))
            }
    }

    fun restoreItem(
        state: HomeState,
        setState: (HomeState) -> Unit,
        eventChannel: Channel<HomeEvent>,
        id: Int,
    ): Flow<*>? {
        val item = state.deletedItems.find { it.id == id } ?: return null
        val items = listOf(item.copy(deleted = false)) + if (item.isFolder) {
            state.deletedItems.childrenOf(item).map { it.copy(deleted = false) }
        } else emptyList()
        val ids = items.map(Item::id)

        return markItemsAsDeletedUseCase(ids, deleted = false)
            .take(1)
            .onStart { setState(state.copy(isLoading = true)) }
            .onEach {
                val newState = closeNestedFolder(state, item)
                setState(
                    newState.copy(
                        isLoading = false,
                        items = (newState.items + items).toSortedTree(newState.orderType.comparator),
                        deletedItems = newState.deletedItems.filter { it.id !in ids },
                    ),
                )
            }
            .catch {
                it.printStackTrace()
                setState(state.copy(isLoading = false))
                eventChannel.send(HomeEvent.ShowSnackbar(R.string.error_restoring_item))
            }
    }
}
