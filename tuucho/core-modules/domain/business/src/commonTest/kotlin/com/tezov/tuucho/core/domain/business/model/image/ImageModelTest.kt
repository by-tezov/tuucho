package com.tezov.tuucho.core.domain.business.model.image

import com.tezov.tuucho.core.domain.business.exception.DomainException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull

class ImageModelTest {
    private fun String.toQuery() = ImageModel.run { toJsonElement() }

    @Test
    fun `from parses command and target`() {
        val model = ImageModel.from("load://file.png", "cache1", null, null)
        assertEquals("load", model.command)
        assertEquals("file.png", model.target)
        assertNull(model.query)
        assertEquals("cache1", model.cacheKey)
        assertNull(model.tags)
        assertNull(model.tagsExcluder)
    }

    @Test
    fun `from parses command target and query`() {
        val model = ImageModel.from("load://file.png?a=1&b=2", "cache1", setOf("tag1"), setOf("exclude1"))
        assertEquals("load", model.command)
        assertEquals("file.png", model.target)
        assertIs<JsonObject>(model.query)
        assertEquals(JsonPrimitive("1"), model.query["a"])
        assertEquals(JsonPrimitive("2"), model.query["b"])
        assertEquals("cache1", model.cacheKey)
        assertEquals(setOf("tag1"), model.tags)
        assertEquals(setOf("exclude1"), model.tagsExcluder)
    }

    @Test
    fun `from rejects invalid string`() {
        assertFailsWith<DomainException.Default> {
            ImageModel.from("invalid", "cache1", null, null)
        }
    }

    @Test
    fun `from factory parses query string`() {
        val model = ImageModel.from(
            command = "load",
            target = "file.png",
            query = "x=1&y=2",
            cacheKey = "cache1",
            tags = setOf("tag1"),
            tagsExcluder = null
        )
        assertIs<JsonObject>(model.query)
        val query = model.query
        assertEquals(JsonPrimitive("1"), query["x"])
        assertEquals(JsonPrimitive("2"), query["y"])
        assertEquals("cache1", model.cacheKey)
        assertEquals(setOf("tag1"), model.tags)
        assertNull(model.tagsExcluder)
    }

    @Test
    fun `query returns null for empty string`() {
        assertNull("".toQuery())
    }

    @Test
    fun `query parses key value map`() {
        val query = "a=1&b=2".toQuery()
        assertIs<JsonObject>(query)
        assertEquals(JsonPrimitive("1"), query["a"])
        assertEquals(JsonPrimitive("2"), query["b"])
    }

    @Test
    fun `query parses list`() {
        val query = "x,y,z".toQuery()
        assertIs<JsonArray>(query)
        assertEquals(listOf("x", "y", "z"), query.map { it.jsonPrimitive.content })
    }

    @Test
    fun `query list ignores empty values`() {
        val query = "a,,c".toQuery()
        assertIs<JsonArray>(query)
        assertEquals(listOf("a", "c"), query.map { it.jsonPrimitive.content })
    }

    @Test
    fun `query parses primitive`() {
        val query = "hello".toQuery()
        assertIs<JsonPrimitive>(query)
        assertEquals("hello", query.content)
    }

    @Test
    fun `toString prints command and target`() {
        val model = ImageModel("cmd", "img.png", null, "cache1", null, null)
        assertEquals("cmd://img.png", model.toString())
    }

    @Test
    fun `toString prints object query`() {
        val query = JsonObject(mapOf("a" to JsonPrimitive("1"), "b" to JsonPrimitive("2")))
        val model = ImageModel("cmd", "img.png", query, "cache1", null, null)
        assertEquals("cmd://img.png?a=1&b=2", model.toString())
    }

    @Test
    fun `toString prints array query`() {
        val query = JsonArray(listOf(JsonPrimitive("x"), JsonPrimitive("y")))
        val model = ImageModel("cmd", "img.png", query, "cache1", null, null)
        assertEquals("cmd://img.png?x,y", model.toString())
    }

    @Test
    fun `toString prints primitive query`() {
        val query = JsonPrimitive("test")
        val model = ImageModel("cmd", "img.png", query, "cache1", null, null)
        assertEquals("cmd://img.png?test", model.toString())
    }

    @Test
    fun `from factory creates model from parameters with query`() {
        val model = ImageModel.from(
            command = "load",
            target = "file.png",
            query = "x=1&y=2",
            cacheKey = "cache1",
            tags = setOf("tag1"),
            tagsExcluder = setOf("exclude1")
        )

        assertEquals("load", model.command)
        assertEquals("file.png", model.target)
        assertIs<JsonObject>(model.query)
        assertEquals(JsonPrimitive("1"), model.query["x"])
        assertEquals(JsonPrimitive("2"), model.query["y"])
        assertEquals("cache1", model.cacheKey)
        assertEquals(setOf("tag1"), model.tags)
        assertEquals(setOf("exclude1"), model.tagsExcluder)
    }

    @Test
    fun `from factory creates model from parameters with null query`() {
        val model = ImageModel.from(
            command = "load",
            target = "file.png",
            query = null,
            cacheKey = "cache1",
            tags = null,
            tagsExcluder = null
        )

        assertEquals("load", model.command)
        assertEquals("file.png", model.target)
        assertNull(model.query)
        assertEquals("cache1", model.cacheKey)
        assertNull(model.tags)
        assertNull(model.tagsExcluder)
    }
}
