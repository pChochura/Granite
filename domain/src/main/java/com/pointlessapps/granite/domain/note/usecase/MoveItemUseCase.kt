package com.pointlessapps.granite.domain.note.usecase

import com.pointlessapps.granite.local.datasource.note.LocalNoteDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MoveItemUseCase(
    private val localDatasource: LocalNoteDatasource,
) {
    suspend operator fun invoke(id: Int, newParentId: Int?) = withContext(Dispatchers.IO) {
        localDatasource.move(id, newParentId)
    }
}
