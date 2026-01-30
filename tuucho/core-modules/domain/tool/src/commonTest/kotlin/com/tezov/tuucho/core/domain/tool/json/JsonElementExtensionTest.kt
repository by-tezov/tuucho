package com.tezov.tuucho.core.domain.tool.json

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JsonElementExtensionTest {
    private val sample = buildJsonObject {
        putJsonObject("user") {
            putJsonObject("profile") {
                put("name", "Alice")
                put("active", true)
            }
            putJsonObject("settings") {
                putJsonObject("preferences") {
                    put("theme", buildJsonObject {
                        put("color", "blue")
                    })
                }
            }
        }
        putJsonArray("tags") {
            add("kotlin")
            add("dsl")
        }
    }

    @Test
    fun `string throws when element is null`() {
        val element: JsonElement? = null
        assertFailsWith<IllegalArgumentException> {
            element.string
        }
    }

    @Test
    fun `booleanOrNull returns null when element is not primitive`() {
        val element = JsonObject(emptyMap())
        assertNull(element.booleanOrNull)
    }

    @Test
    fun `int throws when element is null`() {
        val element: JsonElement? = null
        assertFailsWith<IllegalArgumentException> {
            element.int
        }
    }

    @Test
    fun `floatOrNull returns null when element is not primitive`() {
        val element = JsonArray(emptyList())
        assertNull(element.floatOrNull)
    }

    @Test
    fun `findOrNull returns null for invalid array index`() {
        val path = JsonElementPath("tags/#abc")
        val result = sample.findOrNull(path)
        assertNull(result)
    }

    @Test
    fun `findOrNull returns null when index used on non array`() {
        val path = JsonElementPath("user/#0")
        val result = sample.findOrNull(path)
        assertNull(result)
    }

    @Test
    fun `findOrNull returns null when traversing primitive`() {
        val path = JsonElementPath("user/profile/name/extra")
        val result = sample.findOrNull(path)
        assertNull(result)
    }

    @Test
    fun `replaceOrInsert with empty path replaces root`() {
        val path = JsonElementPath("")
        val updated = sample.replaceOrInsert(path, JsonPrimitive("root"))
        assertEquals("root", updated.string)
    }

    @Test
    fun `replaceOrInsert throws when path contains array index`() {
        val path = JsonElementPath("tags/#0")
        assertFailsWith<IllegalArgumentException> {
            sample.replaceOrInsert(path, JsonPrimitive("oops"))
        }
    }

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

    @Test
    fun `findOrNull returns element for nested path`() {
        val path = JsonElementPath("user/profile/name")
        val result = sample.findOrNull(path)
        assertEquals("Alice", result?.stringOrNull)
    }

    @Test
    fun `findOrNull returns null if path does not exist`() {
        val path = JsonElementPath("user/profile/age")
        val result = sample.findOrNull(path)
        assertNull(result)
    }

    @Test
    fun `findOrNull handles array index path`() {
        val path = JsonElementPath("tags/#1")
        val result = sample.findOrNull(path)
        assertEquals("dsl", result?.stringOrNull)
    }

    @Test
    fun `find returns element or throws if missing`() {
        val path = JsonElementPath("user/settings/preferences/theme/color")
        val result = sample.find(path)
        assertEquals("blue", result.string)

        val missingPath = JsonElementPath("user/settings/preferences/theme/font")
        assertFailsWith<IllegalArgumentException> {
            sample.find(missingPath)
        }
    }

    @Test
    fun `replaceOrInsert updates existing path`() {
        val path = JsonElementPath("user/profile/name")
        val updated = sample.replaceOrInsert(path, JsonPrimitive("Bob"))
        assertEquals("Bob", updated.find(path).string)
    }

    @Test
    fun `replaceOrInsert inserts new deep path`() {
        val path = JsonElementPath("user/profile/address/city")
        val updated = sample.replaceOrInsert(path, JsonPrimitive("Paris"))
        assertEquals("Paris", updated.find(path).string)
    }

    @Test
    fun `replaceOrInsert throws when path traverses primitive`() {
        val invalid = buildJsonObject {
            putJsonObject("user") {
                put("profile", "not-an-object")
            }
        }
        val path = JsonElementPath("user/profile/name")
        assertFailsWith<IllegalArgumentException> {
            invalid.replaceOrInsert(path, JsonPrimitive("oops"))
        }
    }

    @Test
    fun `float returns value when element is primitive`() {
        val element: JsonElement = JsonPrimitive(1.5f)
        assertEquals(1.5f, element.float)
    }

    @Test
    fun `float throws when element is not primitive`() {
        val element: JsonElement = JsonObject(emptyMap())
        assertFailsWith<IllegalArgumentException> {
            element.float
        }
    }

    @Test
    fun `int returns value when element is primitive`() {
        val element: JsonElement = JsonPrimitive(42)
        assertEquals(42, element.int)
    }
}
