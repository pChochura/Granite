package com.pointlessapps.granite.domain.prefs.usecase

import com.pointlessapps.granite.local.datasource.prefs.LocalPrefsDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetDailyNotesEnabledUseCase(
    private val localPrefsDatasource: LocalPrefsDatasource,
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        localPrefsDatasource.getDailyNotesEnabled()
    }
}
