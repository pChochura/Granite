package com.pointlessapps.granite.domain.prefs.usecase

import com.pointlessapps.granite.domain.prefs.PrefsRepository

class CreateDailyNotesFolderUseCase(
    private val prefsRepository: PrefsRepository,
) {
    operator fun invoke() = prefsRepository.createDailyNotesFolder()
}
