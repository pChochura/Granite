package com.pointlessapps.granite.domain.prefs.usecase

import com.pointlessapps.granite.domain.prefs.PrefsRepository

class SetLastOpenedFileUseCase(
    private val prefsRepository: PrefsRepository,
) {
    operator fun invoke(id: Int?) = prefsRepository.setLastOpenedFileId(id)
}
