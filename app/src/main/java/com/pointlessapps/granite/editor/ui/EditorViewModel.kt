package com.pointlessapps.granite.editor.ui

import android.app.Application
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.pointlessapps.granite.R
import com.pointlessapps.granite.domain.note.usecase.CreateDailyNoteUseCase
import com.pointlessapps.granite.domain.note.usecase.CreateItemUseCase
import com.pointlessapps.granite.domain.note.usecase.GetNoteUseCase
import com.pointlessapps.granite.domain.note.usecase.GetTodayDailyNoteUseCase
import com.pointlessapps.granite.domain.note.usecase.UpdateItemUseCase
import com.pointlessapps.granite.domain.tag.usecase.GetDailyNoteTagIdUseCase
import com.pointlessapps.granite.mapper.toTag
import com.pointlessapps.granite.mica.lexer.Lexer
import com.pointlessapps.granite.mica.parser.Parser
import com.pointlessapps.granite.mica.semantics.SemanticAnalyzer
import com.pointlessapps.granite.model.DateProperty
import com.pointlessapps.granite.model.ListProperty
import com.pointlessapps.granite.model.Property
import com.pointlessapps.granite.model.Tag
import com.pointlessapps.granite.navigation.Route
import com.pointlessapps.granite.utils.TextFieldValueParceler
import com.pointlessapps.granite.utils.launchWithDelayedLoading
import com.pointlessapps.granite.utils.mutableStateOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.pointlessapps.granite.ui.R as RC

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
    val tags: List<Tag> = emptyList(),
    val createdAt: Long = -1,
    val updatedAt: Long = -1,
    val isDailyNote: Boolean = false,
    val isLoading: Boolean = false,
) : Parcelable {

    @IgnoredOnParcel
    val properties: List<Property> = listOf(
        DateProperty(
            id = Property.CREATED_AT_ID,
            icon = RC.drawable.ic_calendar,
            name = R.string.created_at_literal,
            date = createdAt,
        ),
        DateProperty(
            id = Property.UPDATED_AT_ID,
            icon = RC.drawable.ic_improve,
            name = R.string.modified_at_literal,
            date = updatedAt,
        ),
        ListProperty(
            id = Property.TAGS_ID,
            icon = RC.drawable.ic_tag,
            name = R.string.tags,
            items = tags.map {
                ListProperty.Item(
                    id = it.id,
                    name = it.name,
                    color = it.color,
                )
            },
        ),
    )
}

internal class EditorViewModel(
    savedStateHandle: SavedStateHandle,
    application: Application,
    arg: Route.Editor,
) : AndroidViewModel(application), KoinComponent {

    private val untitledNotePlaceholder = getApplication<Application>().getString(R.string.untitled)
    private val getTodayDailyNoteUseCase: GetTodayDailyNoteUseCase by inject()
    private val createDailyNoteUseCase: CreateDailyNoteUseCase by inject()
    private val getDailyNoteTagIdUseCase: GetDailyNoteTagIdUseCase by inject()
    private val createItemUseCase: CreateItemUseCase by inject()
    private val updateItemUseCase: UpdateItemUseCase by inject()
    private val getNoteUseCase: GetNoteUseCase by inject()

    var state by savedStateHandle.mutableStateOf(
        EditorState(
            itemId = if (arg is Route.Editor.Note) arg.id else null,
            isDailyNote = arg is Route.Editor.DailyNote,
        ),
    )
        private set

    private val eventChannel = Channel<EditorEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        launchWithDelayedLoading(
            onException = handleErrors(R.string.error_loading_note),
            onShowLoader = { state = state.copy(isLoading = true) },
        ) {
            val note = when (arg) {
                is Route.Editor.Note -> requireNotNull(getNoteUseCase(arg.id))
                is Route.Editor.DailyNote -> getTodayDailyNoteUseCase()
                    ?: createDailyNoteUseCase()

                is Route.Editor.NewNote -> createItemUseCase(
                    name = untitledNotePlaceholder,
                    content = "",
                    parentId = arg.parentId,
                ).first()
            }

            state = state.copy(
                isLoading = false,
                itemId = note.id,
                parentId = note.parentId,
                title = TextFieldValue(note.name),
                content = TextFieldValue(note.content.orEmpty()),
                tags = note.tags.map { it.toTag() },
                createdAt = note.createdAt,
                updatedAt = note.updatedAt,
                isDailyNote = note.tags.find { it.id == getDailyNoteTagIdUseCase() } != null,
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

    fun compile() {
        runCatching {
            Parser(Lexer(state.content.text)).parse()
        }.also {
            it.exceptionOrNull()?.printStackTrace()
            println(it)
            it.getOrNull()?.let(::SemanticAnalyzer)?.analyze()
        }
    }
}
