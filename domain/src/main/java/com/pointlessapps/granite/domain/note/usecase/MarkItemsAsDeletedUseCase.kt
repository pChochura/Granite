package com.pointlessapps.granite.domain.note.usecase

import com.pointlessapps.granite.domain.note.NoteRepository

class MarkItemsAsDeletedUseCase(
    private val noteRepository: NoteRepository,
) {
    operator fun invoke(ids: Set<Int>) = noteRepository.markAsDeleted(ids)
}
