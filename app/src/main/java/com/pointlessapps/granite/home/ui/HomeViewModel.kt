package com.pointlessapps.granite.home.ui

import android.app.Application
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.pointlessapps.granite.R
import com.pointlessapps.granite.domain.model.Note
import com.pointlessapps.granite.domain.note.usecase.CreateItemUseCase
import com.pointlessapps.granite.domain.note.usecase.DeleteItemsUseCase
import com.pointlessapps.granite.domain.note.usecase.DuplicateItemsUseCase
import com.pointlessapps.granite.domain.note.usecase.GetNotesUseCase
import com.pointlessapps.granite.domain.note.usecase.MarkItemsAsDeletedUseCase
import com.pointlessapps.granite.domain.note.usecase.MoveItemUseCase
import com.pointlessapps.granite.domain.note.usecase.UpdateItemUseCase
import com.pointlessapps.granite.domain.prefs.usecase.GetItemsOrderTypeUseCase
import com.pointlessapps.granite.domain.prefs.usecase.SetItemsOrderTypeUseCase
import com.pointlessapps.granite.home.mapper.fromItemOrderType
import com.pointlessapps.granite.home.mapper.toItem
import com.pointlessapps.granite.home.mapper.toItemOrderType
import com.pointlessapps.granite.home.mapper.toItemWithParents
import com.pointlessapps.granite.home.model.Item
import com.pointlessapps.granite.home.model.ItemOrderType
import com.pointlessapps.granite.home.model.ItemWithParents
import com.pointlessapps.granite.home.utils.toSortedTree
import com.pointlessapps.granite.home.utils.withChildrenOf
import com.pointlessapps.granite.navigation.Route
import com.pointlessapps.granite.utils.launch
import com.pointlessapps.granite.utils.launchWithDelayedLoading
import com.pointlessapps.granite.utils.mutableStateOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Parcelize
internal data class HomeState(
    val openedFolderIds: Set<Int> = emptySet(),
    val orderType: ItemOrderType = ItemOrderType.NameAscending,
    val items: List<Item> = emptyList(),
    val deletedItems: List<Item> = emptyList(),
    val isLoading: Boolean = false,
) : Parcelable {

    @IgnoredOnParcel
    val filteredItems = items.filtered()

    private fun List<Item>.filtered() = filter {
        it.parentId !in this.map(Item::id) ||
                it.parentId in openedFolderIds ||
                it.parentId == null
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
    data class ShowSnackbar(@StringRes val message: Int) : HomeEvent
    data class NavigateTo(val route: Route) : HomeEvent
}

internal class HomeViewModel(
    savedStateHandle: SavedStateHandle,
    application: Application,
) : AndroidViewModel(application), KoinComponent {

    private val setItemsOrderTypeUseCase: SetItemsOrderTypeUseCase by inject()
    private val updateItemUseCase: UpdateItemUseCase by inject()
    private val createItemUseCase: CreateItemUseCase by inject()
    private val duplicateItemsUseCase: DuplicateItemsUseCase by inject()
    private val moveItemUseCase: MoveItemUseCase by inject()
    private val markItemsAsDeletedUseCase: MarkItemsAsDeletedUseCase by inject()
    private val deleteItemsUseCase: DeleteItemsUseCase by inject()
    private val getNotesUseCase: GetNotesUseCase by inject()
    private val getItemsOrderTypeUseCase: GetItemsOrderTypeUseCase by inject()

    private val eventChannel = Channel<HomeEvent>()
    val events = eventChannel.receiveAsFlow()

    var state by savedStateHandle.mutableStateOf(HomeState())
        private set

    init {
        loadItems()
    }

    fun loadItems() {
        launchWithDelayedLoading(
            onException = handleErrors(R.string.error_loading_notes),
            onShowLoader = { state = state.copy(isLoading = true) },
        ) {
            val (deletedItems, items) = getNotesUseCase().map(Note::toItem).partition(Item::deleted)
            val orderType = getItemsOrderTypeUseCase().toItemOrderType()
            state = state.copy(
                isLoading = false,
                items = items.toSortedTree(orderType.comparator),
                deletedItems = deletedItems.toSortedTree(orderType.comparator),
                orderType = orderType,
            )
        }
    }

    fun onItemSelected(item: Item) {
        when {
            !item.isFolder -> onFileSelected(item)
            else -> onFolderSelected(item)
        }
    }

    private fun onFileSelected(item: Item) {
        if (item.deleted) {
            // TODO show a dialog whether to restore the item
            eventChannel.trySend(HomeEvent.ShowSnackbar(R.string.not_implemented_yet))
            return
        }

        eventChannel.trySend(HomeEvent.NavigateTo(Route.Editor(item.id)))
    }

    private fun onFolderSelected(item: Item) {
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
        // TODO handle parentId
        eventChannel.trySend(HomeEvent.NavigateTo(Route.Editor()))
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

    fun createFolder(name: String, parentId: Int?) {
        launch(handleErrors(R.string.error_creating_folder)) {
            val folders = createItemUseCase(name = name, content = null, parentId = parentId)
            state = state.copy(
                isLoading = false,
                items = (state.items + folders.map(Note::toItem))
                    .toSortedTree(state.orderType.comparator),
            )
        }
    }

    fun renameItem(id: Int, name: String) {
        launch(handleErrors(R.string.error_renaming_item)) {
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
            )
        }
    }

    fun duplicateItem(id: Int) {
        launch(handleErrors(R.string.error_duplicating_item)) {
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
        launch(handleErrors(R.string.error_deleting_item)) {
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
                openedFolderIds = state.openedFolderIds - (newItems.withChildrenOf(id)
                    ?.map(Item::id) ?: emptySet()),
            )
        }
    }

    fun deleteItemPermanently(id: Int) {
        launch(handleErrors(R.string.error_deleting_item)) {
            val ids = state.deletedItems.withChildrenOf(id)?.map(Item::id) ?: return@launch

            state = state.copy(isLoading = true)
            deleteItemsUseCase(ids)
            state = state.copy(
                isLoading = false,
                deletedItems = state.deletedItems.filter { it.id !in ids },
                openedFolderIds = state.openedFolderIds - ids
            )
        }
    }

    fun restoreItem(id: Int) {
        launch(handleErrors(R.string.error_restoring_item)) {
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
        launch(handleErrors(R.string.error_moving_item)) {
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

    private fun handleErrors(@StringRes errorDescription: Int): (Throwable) -> Unit = {
        it.printStackTrace()
        state = state.copy(isLoading = false)
        eventChannel.trySend(HomeEvent.ShowSnackbar(errorDescription))
    }
}
