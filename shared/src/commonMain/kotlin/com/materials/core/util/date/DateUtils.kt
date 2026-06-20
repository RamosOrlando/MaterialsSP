package com.materials.core.util.date

import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

fun getCurrentDateStr(): String {
    val currentMoment = Clock.System.now()
    val localDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
    val day = localDateTime.day
    val month = localDateTime.month.number
    val year = localDateTime.year
    return "$day/$month/$year"
}
