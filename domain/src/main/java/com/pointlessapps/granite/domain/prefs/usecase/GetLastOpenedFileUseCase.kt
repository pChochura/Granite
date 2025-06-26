package com.pointlessapps.granite.domain.prefs.usecase

import com.pointlessapps.granite.domain.mapper.fromLocal
import com.pointlessapps.granite.local.datasource.note.LocalNoteDatasource
import com.pointlessapps.granite.local.datasource.prefs.LocalPrefsDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetLastOpenedFileUseCase(
    private val localPrefsDatasource: LocalPrefsDatasource,
    private val localNoteDatasource: LocalNoteDatasource,
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        localPrefsDatasource.getLastOpenedFileId()?.let {
            localNoteDatasource.getById(it)?.fromLocal()
        }
    }
}
