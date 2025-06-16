package com.pointlessapps.granite.domain.note.usecase

import com.pointlessapps.granite.domain.note.NoteRepository

class DuplicateItemsUseCase(
    private val noteRepository: NoteRepository,
) {
    operator fun invoke(ids: List<Int>) = noteRepository.duplicate(ids)
}
