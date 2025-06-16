package com.pointlessapps.granite.domain.note.usecase

import com.pointlessapps.granite.domain.note.NoteRepository

class MarkItemsAsDeletedUseCase(
    private val noteRepository: NoteRepository,
) {
    operator fun invoke(ids: List<Int>, deleted: Boolean) =
        noteRepository.markAsDeleted(ids, deleted)
}
