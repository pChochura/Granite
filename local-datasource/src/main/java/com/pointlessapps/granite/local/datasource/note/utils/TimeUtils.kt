package com.pointlessapps.granite.local.datasource.note.utils

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

internal fun getCurrentTimestamp() =
    DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(OffsetDateTime.now())
