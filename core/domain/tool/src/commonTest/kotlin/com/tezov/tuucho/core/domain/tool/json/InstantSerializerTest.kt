package com.tezov.tuucho.core.domain.tool.json

import kotlinx.serialization.json.Json
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class InstantSerializerTest {

    private val json = Json {
        serializersModule = kotlinx.serialization.modules.SerializersModule {
            contextual(Instant::class, InstantSerializer())
        }
    }

    @Test
    fun `serialize instant to ISO string`() {
        val instant = Instant.parse("2025-09-08T12:34:56Z")
        val encoded = json.encodeToString(InstantSerializer(), instant)
        assertEquals("\"2025-09-08T12:34:56Z\"", encoded)
    }

    @Test
    fun `deserialize ISO string to instant`() {
        val input = "\"2025-09-08T12:34:56Z\""
        val decoded = json.decodeFromString(InstantSerializer(), input)
        assertEquals(Instant.parse("2025-09-08T12:34:56Z"), decoded)
    }

    @Test
    fun `round trip serialize and deserialize`() {
        val original = Instant.parse("2025-09-08T00:00:00Z")
        val encoded = json.encodeToString(InstantSerializer(), original)
        val decoded = json.decodeFromString(InstantSerializer(), encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `round trip with random instant`() {
        val randomMillis = Random.nextLong(0, 4102444800000L)
        val original = Instant.fromEpochMilliseconds(randomMillis)
        val encoded = json.encodeToString(InstantSerializer(), original)
        val decoded = json.decodeFromString(InstantSerializer(), encoded)
        assertEquals(original, decoded, "Random instant did not round-trip correctly")
    }
}
