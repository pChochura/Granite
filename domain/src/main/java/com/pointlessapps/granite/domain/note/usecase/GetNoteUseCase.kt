package com.pointlessapps.granite.domain.note.usecase

import com.pointlessapps.granite.domain.mapper.fromLocal
import com.pointlessapps.granite.local.datasource.note.LocalNoteDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetNoteUseCase(
    private val localDatasource: LocalNoteDatasource,
) {
    suspend operator fun invoke(id: Int) = withContext(Dispatchers.IO) {
        localDatasource.getById(id)?.fromLocal()
    }
}
