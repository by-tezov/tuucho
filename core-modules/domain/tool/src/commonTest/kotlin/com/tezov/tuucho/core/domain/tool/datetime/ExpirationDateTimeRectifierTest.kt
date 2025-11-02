package com.tezov.tuucho.core.domain.tool.datetime

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class ExpirationDateTimeRectifierTest {
    private lateinit var sut: ExpirationDateTimeRectifier
    private val fixedNow = Instant.parse("2025-09-06T10:00:00Z")

    @BeforeTest
    fun setUp() {
        sut = ExpirationDateTimeRectifier()
    }

    // ----------------------------
    // Relative durations (fixedNow)
    // ----------------------------
    @Test
    fun `parse 10s adds seconds`() {
        assertEquals("2025-09-06T10:00:10Z", sut.process("10s", fixedNow))
    }

    @Test
    fun `parse 10mn adds minutes`() {
        assertEquals("2025-09-06T10:10:00Z", sut.process("10mn", fixedNow))
    }

    @Test
    fun `parse 5d adds days`() {
        assertEquals("2025-09-11T10:00:00Z", sut.process("5d", fixedNow))
    }

    @Test
    fun `parse 1mth adds months`() {
        assertEquals("2025-10-06T10:00:00Z", sut.process("1mth", fixedNow))
    }

    // ----------------------------
    // Edge cases for rollover
    // ----------------------------
    @Test
    fun `parse 10s near midnight rolls to next day`() {
        val nearMidnight = Instant.parse("2025-09-06T23:59:55Z")
        assertEquals("2025-09-07T00:00:05Z", sut.process("10s", nearMidnight))
    }

    @Test
    fun `parse 10mn near midnight rolls to next day`() {
        val nearMidnight = Instant.parse("2025-09-06T23:55:00Z")
        assertEquals("2025-09-07T00:05:00Z", sut.process("10mn", nearMidnight))
    }

    @Test
    fun `parse 1d at end of month rolls to next month`() {
        val endOfMonth = Instant.parse("2025-01-31T10:00:00Z")
        assertEquals("2025-02-01T10:00:00Z", sut.process("1d", endOfMonth))
    }

    @Test
    fun `parse 1mth at end of January rolls to February`() {
        val janEnd = Instant.parse("2025-01-31T10:00:00Z")
        assertEquals("2025-02-28T10:00:00Z", sut.process("1mth", janEnd))
    }

    @Test
    fun `parse 1mth on leap year February end rolls correctly`() {
        val leapFeb = Instant.parse("2024-01-31T10:00:00Z")
        assertEquals("2024-02-29T10:00:00Z", sut.process("1mth", leapFeb))
    }

    @Test
    fun `parse 1mth in December rolls to next year`() {
        val dec = Instant.parse("2025-12-15T10:00:00Z")
        assertEquals("2026-01-15T10:00:00Z", sut.process("1mth", dec))
    }

    // ----------------------------
    // ISO datetime UTC
    // ----------------------------
    @Test
    fun `parse iso datetime utc is returned as-is`() {
        val input = "2025-09-06T12:34:56Z"
        assertEquals(input, sut.process(input, fixedNow))
    }

    @Test
    fun `parse invalid iso datetime utc throws`() {
        assertFailsWith<IllegalArgumentException> {
            sut.process("nonsense", fixedNow)
        }
    }

    // ----------------------------
    // ISO date
    // ----------------------------
    @Test
    fun `parse iso date adds midnight`() {
        assertEquals("2025-09-06T00:00:00Z", sut.process("2025-09-06", fixedNow))
    }

    @Test
    fun `parse invalid iso date throws`() {
        assertFailsWith<IllegalArgumentException> {
            sut.process("2025-45-45", fixedNow)
        }
    }

    @Test
    fun `parse incomplete iso date throws`() {
        assertFailsWith<IllegalArgumentException> {
            sut.process("2025-45", fixedNow)
        }
    }

    // ----------------------------
    // ISO time
    // ----------------------------
    @Test
    fun `parse iso time today if in future`() {
        assertEquals("2025-09-06T12:00:00Z", sut.process("12:00:00", fixedNow))
    }

    @Test
    fun `parse iso time tomorrow if already passed`() {
        assertEquals("2025-09-07T09:00:00Z", sut.process("09:00:00", fixedNow))
    }

    @Test
    fun `parse invalid iso time throws`() {
        assertFailsWith<IllegalArgumentException> {
            sut.process("25:99:99", fixedNow)
        }
    }

    // ----------------------------
    // Unsupported
    // ----------------------------
    @Test
    fun `parse unsupported format throws`() {
        assertFailsWith<IllegalArgumentException> {
            sut.process("nonsense", fixedNow)
        }
    }

    // ----------------------------
    // Real now tests (non-deterministic)
    // ----------------------------
    @Test
    fun `parse relative duration with real now returns exact iso utc`() {
        val now = Clock.System.now()
        val expected = (now + 5.seconds).toString()
        val result = sut.process("5s", now)
        assertEquals(expected, result)
    }

    @Test
    fun `parse iso date with real now normalizes midnight`() {
        val now = Clock.System.now()
        val date = now.toLocalDateTime(TimeZone.UTC).date
        val expected = LocalDateTime(date.year, date.month, date.day, 0, 0, 0)
            .toInstant(TimeZone.UTC)
            .toString()
        val result = sut.process(date.toString(), now)
        assertEquals(expected, result)
    }
}
