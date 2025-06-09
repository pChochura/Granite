package com.pointlessapps.granite.domain.note

import com.pointlessapps.granite.datasource.note.NoteDatasource
import com.pointlessapps.granite.domain.note.mapper.fromRemote
import com.pointlessapps.granite.domain.note.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface NoteRepository {
    fun getNotes(): Flow<List<Note>>
}

internal class NoteRepositoryImpl(
    private val noteDatasource: NoteDatasource,
) : NoteRepository {
    override fun getNotes() = flow {
        emit(noteDatasource.getNotes().map { it.fromRemote() })
    }.flowOn(Dispatchers.IO)
}
