package com.tezov.tuucho.core.domain.business.model.action

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

class ActionModelTest {
    private fun String.toQuery() = ActionModel.run { toJsonElement() }

    @Test
    fun `from parses command authority target`() {
        val model = ActionModel.from("open://system/settings")
        assertEquals("open", model.command)
        assertEquals("system", model.authority)
        assertEquals("settings", model.target)
        assertNull(model.query)
    }

    @Test
    fun `from parses command and authority`() {
        val model = ActionModel.from("open://system")
        assertEquals("open", model.command)
        assertEquals("system", model.authority)
        assertNull(model.target)
        assertNull(model.query)
    }

    @Test
    fun `from parses command only`() {
        val model = ActionModel.from("open://")
        assertEquals("open", model.command)
        assertNull(model.authority)
        assertNull(model.target)
        assertNull(model.query)
    }

    @Test
    fun `from parses nested target`() {
        val model = ActionModel.from("open://a/b/c")
        assertEquals("a", model.authority)
        assertEquals("b/c", model.target)
    }

    @Test
    fun `from rejects invalid string`() {
        assertFailsWith<DomainException.Default> {
            ActionModel.from("invalid")
        }
    }

    @Test
    fun `from rejects missing command`() {
        assertFailsWith<DomainException.Default> {
            ActionModel.from("://a/b")
        }
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
    fun `query keeps empty value`() {
        val query = "t=".toQuery()
        assertEquals(JsonObject(mapOf("t" to JsonPrimitive(""))), query)
    }

    @Test
    fun `query keeps multiple equals`() {
        val query = "x=1=2".toQuery()
        assertEquals(JsonObject(mapOf("x" to JsonPrimitive("1=2"))), query)
    }

    @Test
    fun `from factory parses string query`() {
        val model = ActionModel.from("edit", "file", "doc", "x=1&y=2")
        assertIs<JsonObject>(model.query)
        assertEquals(JsonPrimitive("1"), model.query["x"])
        assertEquals(JsonPrimitive("2"), model.query["y"])
    }

    @Test
    fun `from factory with null string query keeps query null`() {
        val model = ActionModel.from(
            command = "edit",
            authority = "file",
            target = "doc"
        )
        assertNull(model.query)
    }

    @Test
    fun `toString prints command only`() {
        assertEquals("cmd://", ActionModel("cmd", null, null, null).toString())
    }

    @Test
    fun `toString prints authority`() {
        assertEquals("cmd://auth", ActionModel("cmd", "auth", null, null).toString())
    }

    @Test
    fun `toString prints authority and target`() {
        assertEquals("cmd://auth/tgt", ActionModel("cmd", "auth", "tgt", null).toString())
    }

    @Test
    fun `toString prints primitive query`() {
        val query = JsonPrimitive("x")
        val model = ActionModel("cmd", "auth", "tgt", query)
        assertEquals("cmd://auth/tgt?x", model.toString())
    }

    @Test
    fun `toString prints object query`() {
        val query = JsonObject(mapOf("a" to JsonPrimitive("1"), "b" to JsonPrimitive("2")))
        val model = ActionModel("cmd", "auth", "tgt", query)
        assertEquals("cmd://auth/tgt?a=1&b=2", model.toString())
    }

    @Test
    fun `toString prints array query`() {
        val query = JsonArray(listOf(JsonPrimitive("a"), JsonPrimitive("b")))
        val model = ActionModel("cmd", "auth", "tgt", query)
        assertEquals("cmd://auth/tgt?a,b", model.toString())
    }
}
