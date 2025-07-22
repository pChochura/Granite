package com.pointlessapps.granite.editor.ui

import android.app.Application
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.pointlessapps.granite.R
import com.pointlessapps.granite.domain.note.usecase.CreateItemUseCase
import com.pointlessapps.granite.domain.note.usecase.GetNoteUseCase
import com.pointlessapps.granite.domain.note.usecase.UpdateItemUseCase
import com.pointlessapps.granite.navigation.Route
import com.pointlessapps.granite.utils.TextFieldValueParceler
import com.pointlessapps.granite.utils.launchWithDelayedLoading
import com.pointlessapps.granite.utils.mutableStateOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal sealed interface EditorEvent {
    data class ShowSnackbar(@StringRes val message: Int) : EditorEvent
    data class NavigateTo(val route: Route) : EditorEvent
}

@Parcelize
internal data class EditorState(
    val itemId: Int?,
    val parentId: Int? = null,
    val title: @WriteWith<TextFieldValueParceler> TextFieldValue = TextFieldValue(),
    val content: @WriteWith<TextFieldValueParceler> TextFieldValue = TextFieldValue(),
    val isLoading: Boolean = false,
) : Parcelable

internal class EditorViewModel(
    savedStateHandle: SavedStateHandle,
    application: Application,
    itemId: Int?,
) : AndroidViewModel(application), KoinComponent {

    private val untitledNotePlaceholder = getApplication<Application>().getString(R.string.untitled)
    private val createItemUseCase: CreateItemUseCase by inject()
    private val updateItemUseCase: UpdateItemUseCase by inject()
    private val getNoteUseCase: GetNoteUseCase by inject()

    var state by savedStateHandle.mutableStateOf(
        EditorState(itemId = itemId),
    )
        private set

    private val eventChannel = Channel<EditorEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        launchWithDelayedLoading(
            onException = handleErrors(R.string.error_loading_note),
            onShowLoader = { state = state.copy(isLoading = true) },
        ) {
            val note = if (itemId != null) {
                requireNotNull(getNoteUseCase(itemId))
            } else {
                createItemUseCase(
                    name = untitledNotePlaceholder,
                    content = "",
                    parentId = null,
                ).first()
            }
            state = state.copy(
                itemId = note.id,
                parentId = note.parentId,
                title = TextFieldValue(note.name),
                content = TextFieldValue(note.content.orEmpty()),
            )
        }
    }

    fun onContentChanged(value: TextFieldValue) {
        state = state.copy(content = value)
    }

    fun onTitleChanged(value: TextFieldValue) {
        state = state.copy(title = value)
    }

    fun saveNote() {
        launchWithDelayedLoading(
            onException = handleErrors(R.string.error_saving_note),
            onShowLoader = { state = state.copy(isLoading = true) },
        ) {
            val note = updateItemUseCase(
                id = requireNotNull(state.itemId),
                name = state.title.text.ifBlank { untitledNotePlaceholder },
                content = state.content.text,
                parentId = state.parentId,
            )
            state = state.copy(
                isLoading = false,
                title = TextFieldValue(note.name),
                content = TextFieldValue(note.content.orEmpty()),
            )
        }
    }

    private fun handleErrors(@StringRes errorDescription: Int): (Throwable) -> Unit = {
        it.printStackTrace()
        state = state.copy(isLoading = false)
        eventChannel.trySend(EditorEvent.ShowSnackbar(errorDescription))
    }
}
