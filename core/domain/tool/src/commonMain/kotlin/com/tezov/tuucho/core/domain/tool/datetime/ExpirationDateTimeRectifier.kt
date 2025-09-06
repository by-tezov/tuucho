package com.tezov.tuucho.core.domain.tool.datetime

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class ExpirationDateTimeRectifier() {

    fun process(
        input: String,
        now: Instant = Clock.System.now(),
    ): String {
        return when {
            // ---- relative durations ----
            input.endsWith("s") -> {
                val value = input.removeSuffix("s").toLong()
                (now + value.seconds).toString()
            }

            input.endsWith("mn") -> {
                val value = input.removeSuffix("mn").toLong()
                (now + value.minutes).toString()
            }

            input.endsWith("d") -> {
                val value = input.removeSuffix("d").toLong()
                (now + value.days).toString()
            }

            input.endsWith("mth") -> {
                val value = input.removeSuffix("mth").toInt()
                now.plus(value, DateTimeUnit.MONTH, TimeZone.UTC).toString()
            }

            // ---- iso datetime utc (already valid) ----
            runCatching { Instant.parse(input) }
                .map { it.toString() }
                .getOrNull() != null -> input

            // ---- iso date (add midnight) ----
            Regex("""\d{4}-\d{2}-\d{2}""").matches(input) -> {
                try {
                    LocalDateTime.parse("${input}T00:00:00").toInstant(TimeZone.UTC).toString()
                } catch (_: Exception) {
                    throw IllegalArgumentException("Invalid ISO date: $input")
                }
            }

            // ---- iso time only ----
            Regex("""\d{2}:\d{2}(:\d{2})?""").matches(input) -> {
                try {
                    val parts = input.split(":").map { it.toInt() }
                    val (h, m, s) = when (parts.size) {
                        2 -> Triple(parts[0], parts[1], 0)
                        3 -> Triple(parts[0], parts[1], parts[2])
                        else -> throw IllegalArgumentException("Invalid time format: $input")
                    }
                    val today = now.toLocalDateTime(TimeZone.UTC).date
                    var candidate = LocalDateTime(today.year, today.month, today.day, h, m, s)
                    val candidateInstant = candidate.toInstant(TimeZone.UTC)
                    if (candidateInstant <= now) {
                        val tomorrow = today.plus(1, DateTimeUnit.DAY)
                        candidate =
                            LocalDateTime(tomorrow.year, tomorrow.month, tomorrow.day, h, m, s)
                    }
                    candidate.toInstant(TimeZone.UTC).toString()
                } catch (_: Exception) {
                    throw IllegalArgumentException("Invalid ISO time: $input")
                }
            }

            else -> throw IllegalArgumentException("Unsupported transient value: $input")
        }
    }
}