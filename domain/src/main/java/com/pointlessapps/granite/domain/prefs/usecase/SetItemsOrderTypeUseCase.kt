package com.pointlessapps.granite.domain.prefs.usecase

import com.pointlessapps.granite.domain.prefs.PrefsRepository
import com.pointlessapps.granite.domain.prefs.model.ItemOrderType

class SetItemsOrderTypeUseCase(
    private val prefsRepository: PrefsRepository,
) {
    operator fun invoke(itemOrderType: ItemOrderType) = prefsRepository.setItemsOrderType(itemOrderType)
}
