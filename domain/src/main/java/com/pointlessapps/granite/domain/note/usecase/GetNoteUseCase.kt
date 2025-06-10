package com.pointlessapps.granite.domain.note.usecase

import com.pointlessapps.granite.domain.note.NoteRepository

class GetNoteUseCase(
    private val noteRepository: NoteRepository,
) {
    operator fun invoke(id: Int) = noteRepository.getNote(id)
}
