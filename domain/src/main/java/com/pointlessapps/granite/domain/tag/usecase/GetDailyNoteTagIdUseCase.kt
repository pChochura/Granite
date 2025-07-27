package com.pointlessapps.granite.domain.tag.usecase

import com.pointlessapps.granite.local.datasource.note.LocalTagDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetDailyNoteTagIdUseCase(
    private val localTagDatasource: LocalTagDatasource,
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        localTagDatasource.getDailyNoteTagId()
    }
}
