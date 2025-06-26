package com.pointlessapps.granite.domain.note.usecase

import com.pointlessapps.granite.domain.exception.ItemNotFoundException
import com.pointlessapps.granite.domain.mapper.fromLocal
import com.pointlessapps.granite.local.datasource.note.LocalNoteDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateItemUseCase(
    private val localDatasource: LocalNoteDatasource,
) {
    suspend operator fun invoke(
        id: Int,
        name: String,
        content: String?,
        parentId: Int?,
    ) = withContext(Dispatchers.IO) {
        localDatasource.update(
            id = id,
            name = name,
            content = content,
            parentId = parentId,
        )?.fromLocal() ?: throw ItemNotFoundException()
    }
}
