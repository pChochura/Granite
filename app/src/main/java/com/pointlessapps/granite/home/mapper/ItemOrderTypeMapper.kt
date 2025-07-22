package com.pointlessapps.granite.home.mapper

import com.pointlessapps.granite.home.model.ItemOrderType
import com.pointlessapps.granite.domain.prefs.model.ItemOrderType as DomainItemOrderType

internal fun DomainItemOrderType.toItemOrderType() = when (this) {
    DomainItemOrderType.NameAscending -> ItemOrderType.NameAscending
    DomainItemOrderType.NameDescending -> ItemOrderType.NameDescending
    DomainItemOrderType.CreateDateAscending -> ItemOrderType.CreateDateAscending
    DomainItemOrderType.CreateDateDescending -> ItemOrderType.CreateDateDescending
}

internal fun ItemOrderType.fromItemOrderType() = when (this) {
    ItemOrderType.NameAscending -> DomainItemOrderType.NameAscending
    ItemOrderType.NameDescending -> DomainItemOrderType.NameDescending
    ItemOrderType.CreateDateAscending -> DomainItemOrderType.CreateDateAscending
    ItemOrderType.CreateDateDescending -> DomainItemOrderType.CreateDateDescending
}
