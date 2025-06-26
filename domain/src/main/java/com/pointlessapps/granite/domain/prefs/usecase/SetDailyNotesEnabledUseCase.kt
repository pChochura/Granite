package com.pointlessapps.granite.domain.prefs.usecase

import com.pointlessapps.granite.local.datasource.prefs.LocalPrefsDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SetDailyNotesEnabledUseCase(
    private val localPrefsDatasource: LocalPrefsDatasource,
) {
    suspend operator fun invoke(enabled: Boolean) = withContext(Dispatchers.IO) {
        localPrefsDatasource.setDailyNotesEnabled(enabled)
    }
}
