package com.pointlessapps.granite.domain.note.usecase

import com.pointlessapps.granite.domain.note.NoteRepository

class UpdateItemUseCase(
    private val noteRepository: NoteRepository,
) {
    operator fun invoke(id: Int, name: String, content: String, parentId: Int?) =
        noteRepository.update(id, name, content, parentId)
}
