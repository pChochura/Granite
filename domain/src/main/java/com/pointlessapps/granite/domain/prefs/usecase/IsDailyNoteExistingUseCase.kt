package com.pointlessapps.granite.domain.prefs.usecase

import com.pointlessapps.granite.local.datasource.note.LocalNoteDatasource
import com.pointlessapps.granite.local.datasource.prefs.LocalPrefsDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IsDailyNoteExistingUseCase(
    private val localPrefsDatasource: LocalPrefsDatasource,
    private val localNoteDatasource: LocalNoteDatasource,
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        localPrefsDatasource.getDailyNotesFolderId()?.let {
            localNoteDatasource.findInFolderByName(
                name = SimpleDateFormat(
                    localPrefsDatasource.getDailyNotesNameFormat(),
                    Locale.getDefault(),
                ).format(Date()),
                folderId = it,
            ) != null
        } == true
    }
}
