package com.pointlessapps.granite.domain.prefs.usecase

import com.pointlessapps.granite.domain.prefs.PrefsRepository

class SetDailyNotesFolderNameUseCase(
    private val prefsRepository: PrefsRepository,
) {
    operator fun invoke(name: String, renameExisting: Boolean) =
        prefsRepository.setDailyNotesFolderName(name, renameExisting)
}
