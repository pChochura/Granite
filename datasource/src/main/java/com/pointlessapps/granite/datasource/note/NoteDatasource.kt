package com.pointlessapps.granite.datasource.note

import com.pointlessapps.granite.datasource.note.model.Note

interface NoteDatasource {
    suspend fun getNote(id: Int): Note?
    suspend fun getNotes(): List<Note>
}