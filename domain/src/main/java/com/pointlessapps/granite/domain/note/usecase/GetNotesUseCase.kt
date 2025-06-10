package com.pointlessapps.granite.domain.note.usecase

import com.pointlessapps.granite.domain.note.NoteRepository

class GetNotesUseCase(
    private val noteRepository: NoteRepository,
) {
    operator fun invoke() = noteRepository.getAll()
}
