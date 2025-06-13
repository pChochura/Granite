package com.pointlessapps.granite.domain.note.usecase

import com.pointlessapps.granite.domain.note.NoteRepository

class CreateItemUseCase(
    private val noteRepository: NoteRepository,
) {
    operator fun invoke(name: String, content: String?, parentId: Int?) =
        noteRepository.create(name, content, parentId)
}
