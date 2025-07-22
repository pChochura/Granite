package com.pointlessapps.granite.domain.note.usecase

import com.pointlessapps.granite.domain.mapper.fromLocal
import com.pointlessapps.granite.local.datasource.note.LocalNoteDatasource
import com.pointlessapps.granite.local.datasource.note.entity.NoteWithTagsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MarkItemsAsDeletedUseCase(
    private val localDatasource: LocalNoteDatasource,
) {
    suspend operator fun invoke(ids: List<Int>, deleted: Boolean) = withContext(Dispatchers.IO) {
        localDatasource.markAsDeleted(ids, deleted).map(NoteWithTagsEntity::fromLocal)
    }
}
