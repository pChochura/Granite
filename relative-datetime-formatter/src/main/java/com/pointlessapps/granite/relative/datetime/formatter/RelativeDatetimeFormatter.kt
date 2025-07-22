package com.pointlessapps.granite.relative.datetime.formatter

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

object RelativeDatetimeFormatter {
    fun formatDateTime(time: Long): Result {
        val now = LocalDateTime.now(ZoneOffset.UTC)
        val then = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC)

        val diffMinutes = ChronoUnit.MINUTES.between(then, now)
        val diffHours = ChronoUnit.HOURS.between(then, now)
        val diffDays = ChronoUnit.DAYS.between(then, now)

        return when {
            diffMinutes < 1 -> Result.LessThanMinuteAgo
            diffMinutes < 60 -> Result.MinutesAgo(diffMinutes.toInt())
            diffHours < 24 -> Result.HoursAgo(diffHours.toInt())
            diffDays == 1L -> Result.Yesterday
            diffDays < 7 -> Result.DaysAgo(diffDays.toInt())
            diffDays == 7L -> Result.LastWeek
            else -> Result.Absolute(time)
        }
    }

    sealed interface Result {
        data object LessThanMinuteAgo : Result
        data object Yesterday : Result
        data object LastWeek : Result
        data class MinutesAgo(val minutes: Int) : Result
        data class HoursAgo(val hours: Int) : Result
        data class DaysAgo(val days: Int) : Result
        data class Absolute(val time: Long) : Result
    }
}
