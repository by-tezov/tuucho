package com.tezov.tuucho.core.domain.tool.json

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class JsonElementExtensionTest {
    private val sample = JsonObject(
        mapOf(
            "user" to JsonObject(
                mapOf(
                    "profile" to JsonObject(
                        mapOf(
                            "name" to JsonPrimitive("Alice"),
                            "active" to JsonPrimitive(true)
                        )
                    )
                )
            )
        )
    )

    @Test
    fun `findOrNull with long path returns element`() {
        val path = JsonElementPath("user/profile/name")
        val result = sample.findOrNull(path)
        assertEquals("Alice", result?.stringOrNull)
    }

    @Test
    fun `find with long path returns element`() {
        val path = JsonElementPath("user/profile/active")
        val result = sample.find(path)
        assertTrue(result.boolean)
    }

    @Test
    fun `replaceOrInsert replaces existing long path`() {
        val path = JsonElementPath("user/profile/name")
        val updated = sample.replaceOrInsert(path, JsonPrimitive("Bob"))
        assertEquals("Bob", updated.find(path).string)
    }

    @Test
    fun `replaceOrInsert inserts new deep long path`() {
        val path = JsonElementPath("user/settings/preferences/theme/color")
        val updated = sample.replaceOrInsert(path, JsonPrimitive("dark"))
        assertEquals("dark", updated.find(path).string)
    }

    @Test
    fun `replaceOrInsert on long path creates intermediate objects`() {
        val path = JsonElementPath("user/profile/details/address/city")
        val updated = sample.replaceOrInsert(path, JsonPrimitive("Paris"))
        assertEquals("Paris", updated.find(path).string)
    }

    @Test
    fun `replaceOrInsert throws when long path traverses primitive`() {
        val base = JsonObject(
            mapOf(
                "user" to JsonObject(
                    mapOf(
                        "profile" to JsonPrimitive("not-an-object")
                    )
                )
            )
        )
        val path = JsonElementPath("user/profile/name")
        assertFailsWith<IllegalArgumentException> {
            base.replaceOrInsert(path, JsonPrimitive("oops"))
        }
    }
}
