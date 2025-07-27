package com.pointlessapps.granite.domain.note.usecase

import com.pointlessapps.granite.domain.exception.ItemNotCreatedException
import com.pointlessapps.granite.domain.mapper.fromLocal
import com.pointlessapps.granite.local.datasource.note.LocalNoteDatasource
import com.pointlessapps.granite.local.datasource.prefs.LocalPrefsDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GetDailyNoteUseCase(
    private val localNoteDatasource: LocalNoteDatasource,
    private val localPrefsDatasource: LocalPrefsDatasource,
) {
    suspend operator fun invoke(date: Date = Date()) = withContext(Dispatchers.IO) {
        localNoteDatasource.findInFolderByName(
            name = SimpleDateFormat(
                localPrefsDatasource.getDailyNotesNameFormat(),
                Locale.getDefault(),
            ).format(date),
            folderId = localPrefsDatasource.getDailyNotesFolderId(),
        )?.fromLocal() ?: throw ItemNotCreatedException()
    }
}
