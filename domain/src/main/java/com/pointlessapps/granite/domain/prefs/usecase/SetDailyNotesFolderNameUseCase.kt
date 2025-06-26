package com.pointlessapps.granite.domain.prefs.usecase

import com.pointlessapps.granite.domain.exception.ItemNotRenamedException
import com.pointlessapps.granite.local.datasource.note.LocalNoteDatasource
import com.pointlessapps.granite.local.datasource.prefs.LocalPrefsDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SetDailyNotesFolderNameUseCase(
    private val localPrefsDatasource: LocalPrefsDatasource,
    private val localNoteDatasource: LocalNoteDatasource,
) {
    suspend operator fun invoke(
        name: String,
        renameExisting: Boolean,
    ) = withContext(Dispatchers.IO) {
        if (renameExisting) {
            val existingFolderId = localPrefsDatasource.getDailyNotesFolderId()
            if (existingFolderId != null) {
                localNoteDatasource.updateName(
                    id = existingFolderId,
                    name = name,
                ) ?: throw ItemNotRenamedException()
            }
        }

        localPrefsDatasource.setDailyNotesFolderName(name)
    }
}
