package com.pointlessapps.granite.domain.utils

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

internal fun String.formatDateAsTimestamp() =
    DateTimeFormatter.ISO_OFFSET_DATE_TIME
        .parse(this)
        .getLong(ChronoField.INSTANT_SECONDS) * 1000 // Convert from seconds to milliseconds

internal fun Long.formatTimestampAsDate() = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(
    OffsetDateTime.of(
        // Convert from milliseconds back to seconds
        LocalDateTime.ofEpochSecond(this / 1000, 0, ZoneOffset.UTC),
        ZoneOffset.UTC,
    ),
)
