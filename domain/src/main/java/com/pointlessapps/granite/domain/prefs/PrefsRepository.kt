package com.pointlessapps.granite.domain.prefs

import com.pointlessapps.granite.domain.model.Note
import com.pointlessapps.granite.domain.note.NoteRepository
import com.pointlessapps.granite.local.datasource.prefs.LocalPrefsDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface PrefsRepository {
    fun setLastOpenedFileId(id: Int?): Flow<Unit>
    fun getLastOpenedFile(): Flow<Note?>
}

internal class PrefsRepositoryImpl(
    private val noteRepository: NoteRepository,
    private val localPrefsDatasource: LocalPrefsDatasource,
) : PrefsRepository {
    override fun setLastOpenedFileId(id: Int?) = flow {
        emit(localPrefsDatasource.setLastOpenedFileId(id))
    }.flowOn(Dispatchers.IO)

    override fun getLastOpenedFile() = flow {
        val id = localPrefsDatasource.getLastOpenedFileId() ?: return@flow emit(null)
        emitAll(noteRepository.getById(id))
    }.flowOn(Dispatchers.IO)
}
