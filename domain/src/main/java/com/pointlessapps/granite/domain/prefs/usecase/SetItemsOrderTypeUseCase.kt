package com.pointlessapps.granite.domain.prefs.usecase

import com.pointlessapps.granite.domain.prefs.model.ItemOrderType
import com.pointlessapps.granite.local.datasource.prefs.LocalPrefsDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SetItemsOrderTypeUseCase(
    private val localPrefsDatasource: LocalPrefsDatasource,
) {
    suspend operator fun invoke(itemOrderType: ItemOrderType) = withContext(Dispatchers.IO) {
        localPrefsDatasource.setItemsOrderTypeIndex(itemOrderType.index)
    }
}
