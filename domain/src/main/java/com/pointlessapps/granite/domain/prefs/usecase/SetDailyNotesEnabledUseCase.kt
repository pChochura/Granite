package com.pointlessapps.granite.domain.prefs.usecase

import com.pointlessapps.granite.domain.prefs.PrefsRepository

class SetDailyNotesEnabledUseCase(
    private val prefsRepository: PrefsRepository,
) {
    operator fun invoke(enabled: Boolean) = prefsRepository.setDailyNotesEnabled(enabled)
}
