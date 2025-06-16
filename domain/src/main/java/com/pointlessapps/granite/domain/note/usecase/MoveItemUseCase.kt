package com.pointlessapps.granite.domain.note.usecase

import com.pointlessapps.granite.domain.note.NoteRepository

class MoveItemUseCase(
    private val noteRepository: NoteRepository,
) {
    operator fun invoke(id: Int, newParentId: Int) = noteRepository.move(id, newParentId)
}
