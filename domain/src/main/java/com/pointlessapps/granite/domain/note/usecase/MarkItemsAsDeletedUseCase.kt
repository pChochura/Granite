package com.pointlessapps.granite.domain.note.usecase

import com.pointlessapps.granite.local.datasource.note.LocalNoteDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MarkItemsAsDeletedUseCase(
    private val localDatasource: LocalNoteDatasource,
) {
    suspend operator fun invoke(ids: List<Int>, deleted: Boolean) = withContext(Dispatchers.IO) {
        localDatasource.markAsDeleted(ids, deleted)
    }
}
