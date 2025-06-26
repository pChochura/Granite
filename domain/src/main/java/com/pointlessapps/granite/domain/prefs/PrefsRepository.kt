package com.pointlessapps.granite.domain.prefs

import com.pointlessapps.granite.domain.model.Note
import com.pointlessapps.granite.domain.note.NoteRepository
import com.pointlessapps.granite.domain.exception.ItemNotCreatedException
import com.pointlessapps.granite.domain.exception.ItemNotRenamedException
import com.pointlessapps.granite.domain.prefs.model.ItemOrderType
import com.pointlessapps.granite.local.datasource.prefs.LocalPrefsDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface PrefsRepository {
    fun setLastOpenedFileId(id: Int?): Flow<Unit>
    fun getLastOpenedFile(): Flow<Note?>

    fun setItemsOrderType(orderType: ItemOrderType): Flow<Unit>
    fun getItemsOrderType(): Flow<ItemOrderType>

    fun getDailyNotesEnabled(): Flow<Boolean>
    fun setDailyNotesEnabled(enabled: Boolean): Flow<Unit>
    fun createDailyNotesFolder(): Flow<Note?>
    fun getDailyNotesFolder(): Flow<Note?>
    fun setDailyNotesFolderName(name: String, renameExisting: Boolean): Flow<Unit>
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
        emit(noteRepository.getById(id).firstOrNull())
    }.flowOn(Dispatchers.IO)

    override fun setItemsOrderType(orderType: ItemOrderType) = flow {
        emit(localPrefsDatasource.setItemsOrderTypeIndex(orderType.index))
    }.flowOn(Dispatchers.IO)

    override fun getItemsOrderType() = flow {
        emit(ItemOrderType.fromIndex(localPrefsDatasource.getItemsOrderTypeIndex()))
    }.flowOn(Dispatchers.IO)

    override fun getDailyNotesEnabled() = flow {
        emit(localPrefsDatasource.getDailyNotesEnabled())
    }.flowOn(Dispatchers.IO)

    override fun setDailyNotesEnabled(enabled: Boolean) = flow {
        emit(localPrefsDatasource.setDailyNotesEnabled(enabled))
    }.flowOn(Dispatchers.IO)

    override fun createDailyNotesFolder() = flow {
        val newFolder = noteRepository.create(
            name = localPrefsDatasource.getDailyNotesFolderName(),
            content = null,
            parentId = null,
        ).firstOrNull()?.firstOrNull() ?: throw ItemNotCreatedException()
        localPrefsDatasource.setDailyNotesFolderId(newFolder.id)
        emit(newFolder)
    }.flowOn(Dispatchers.IO)

    override fun getDailyNotesFolder() = flow {
        emit(
            localPrefsDatasource.getDailyNotesFolderId()?.let {
                noteRepository.getById(it).firstOrNull()
            },
        )
    }.flowOn(Dispatchers.IO)

    override fun setDailyNotesFolderName(name: String, renameExisting: Boolean) = flow {
        if (renameExisting) {
            val existingFolderId = localPrefsDatasource.getDailyNotesFolderId()
            if (existingFolderId != null) {
                noteRepository.updateName(
                    id = existingFolderId,
                    name = name,
                ).firstOrNull() ?: throw ItemNotRenamedException()
            }
        }

        emit(localPrefsDatasource.setDailyNotesFolderName(name))
    }.flowOn(Dispatchers.IO)
}
