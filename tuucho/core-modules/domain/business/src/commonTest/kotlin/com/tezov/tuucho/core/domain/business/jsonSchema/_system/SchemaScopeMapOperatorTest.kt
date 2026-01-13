@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business.jsonSchema._system

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SchemaScopeMapOperatorTest {
    @BeforeTest
    fun setup() {
    }

    @AfterTest
    fun tearDown() {
    }

    @Test
    fun `element returns root child when moveOnRoot is true and key exists`() {
        val source = JsonObject(mapOf("rootKey" to JsonPrimitive("value")))
        val argument = SchemaScopeArgument(
            element = source,
            moveOnRoot = true,
            mapOperator = null
        )
        val operator = SchemaScopeMapOperator(
            root = "rootKey",
            argument = argument
        )
        val resultElement = operator.element
        assertEquals(JsonPrimitive("value"), resultElement)
    }

    @Test
    fun `element returns JsonNull when moveOnRoot is true and key missing`() {
        val source = JsonObject(emptyMap())
        val argument = SchemaScopeArgument(
            element = source,
            moveOnRoot = true,
            mapOperator = null
        )
        val operator = SchemaScopeMapOperator(
            root = "missing",
            argument = argument
        )
        val resultElement = operator.element
        assertEquals(JsonNull, resultElement)
    }

    @Test
    fun `element returns original element when moveOnRoot is false`() {
        val source = JsonPrimitive("value")
        val argument = SchemaScopeArgument(
            element = source,
            moveOnRoot = false,
            mapOperator = null
        )
        val operator = SchemaScopeMapOperator(
            root = "ignored",
            argument = argument
        )
        val resultElement = operator.element
        assertEquals(source, resultElement)
    }

    @Test
    fun `contains returns true when key exists in json object`() {
        val source = JsonObject(mapOf("alpha" to JsonPrimitive(1)))
        val argument = SchemaScopeArgument(
            element = source,
            moveOnRoot = false,
            mapOperator = null
        )
        val operator = SchemaScopeMapOperator(
            root = "",
            argument = argument
        )
        assertTrue(operator.contains("alpha"))
    }

    @Test
    fun `contains returns false when key missing`() {
        val source = JsonObject(mapOf("alpha" to JsonPrimitive(1)))
        val argument = SchemaScopeArgument(
            element = source,
            moveOnRoot = false,
            mapOperator = null
        )
        val operator = SchemaScopeMapOperator(
            root = "",
            argument = argument
        )
        assertFalse(operator.contains("beta"))
    }

    @Test
    fun `write adds or replaces value`() {
        val source = JsonObject(mapOf("a" to JsonPrimitive(1)))
        val argument = SchemaScopeArgument(
            element = source,
            moveOnRoot = false,
            mapOperator = null
        )
        val operator = SchemaScopeMapOperator(
            root = "",
            argument = argument
        )
        operator.write("a", JsonPrimitive(5))
        operator.write("b", JsonPrimitive("new"))
        assertEquals(JsonPrimitive(5), operator.read("a"))
        assertEquals(JsonPrimitive("new"), operator.read("b"))
        assertTrue(operator.hasBeenChanged)
    }

    @Test
    fun `read returns existing value`() {
        val source = JsonObject(mapOf("x" to JsonPrimitive("hello")))
        val argument = SchemaScopeArgument(
            element = source,
            moveOnRoot = false,
            mapOperator = null
        )
        val operator = SchemaScopeMapOperator(
            root = "",
            argument = argument
        )
        val result = operator.read("x")
        assertEquals(JsonPrimitive("hello"), result)
    }

    @Test
    fun `read returns null for missing key`() {
        val source = JsonObject(mapOf("x" to JsonPrimitive("hello")))
        val argument = SchemaScopeArgument(
            element = source,
            moveOnRoot = false,
            mapOperator = null
        )
        val operator = SchemaScopeMapOperator(
            root = "",
            argument = argument
        )
        val result = operator.read("missing")
        assertNull(result)
    }

    @Test
    fun `remove deletes existing key`() {
        val source = JsonObject(mapOf("x" to JsonPrimitive(1), "y" to JsonPrimitive(2)))
        val argument = SchemaScopeArgument(
            element = source,
            moveOnRoot = false,
            mapOperator = null
        )
        val operator = SchemaScopeMapOperator(
            root = "",
            argument = argument
        )
        operator.remove("x")
        assertNull(operator.read("x"))
        assertEquals(JsonPrimitive(2), operator.read("y"))
        assertTrue(operator.hasBeenChanged)
    }

    @Test
    fun `remove no-op when key missing`() {
        val source = JsonObject(mapOf("a" to JsonPrimitive(1)))
        val argument = SchemaScopeArgument(
            element = source,
            moveOnRoot = false,
            mapOperator = null
        )
        val operator = SchemaScopeMapOperator(
            root = "",
            argument = argument
        )
        operator.remove("missing")
        assertEquals(JsonPrimitive(1), operator.read("a"))
        assertTrue(operator.hasBeenChanged)
    }

    @Test
    fun `collect returns original json object when unchanged`() {
        val original = JsonObject(mapOf("k" to JsonPrimitive("v")))
        val argument = SchemaScopeArgument(
            element = original,
            moveOnRoot = false,
            mapOperator = null
        )
        val operator = SchemaScopeMapOperator(
            root = "",
            argument = argument
        )
        val result = operator.collect()
        assertEquals(original, result)
    }

    @Test
    fun `collect returns updated map when changed`() {
        val original = JsonObject(mapOf("k" to JsonPrimitive("v")))
        val argument = SchemaScopeArgument(
            element = original,
            moveOnRoot = false,
            mapOperator = null
        )
        val operator = SchemaScopeMapOperator(
            root = "",
            argument = argument
        )
        operator.write("k", JsonPrimitive("updated"))
        operator.write("n", JsonPrimitive(10))
        val result = operator.collect()
        assertEquals(
            JsonObject(
                mapOf(
                    "k" to JsonPrimitive("updated"),
                    "n" to JsonPrimitive(10)
                )
            ),
            result
        )
    }

    @Test
    fun `collect returns empty object when initial element is primitive`() {
        val source = JsonPrimitive(99)
        val argument = SchemaScopeArgument(
            element = source,
            moveOnRoot = false,
            mapOperator = null
        )
        val operator = SchemaScopeMapOperator(
            root = "",
            argument = argument
        )
        val result = operator.collect()
        assertEquals(JsonObject(emptyMap()), result)
    }

    @Test
    fun `collect returns empty object when initial element is array`() {
        val source = JsonArray(listOf(JsonPrimitive(1)))
        val argument = SchemaScopeArgument(
            element = source,
            moveOnRoot = false,
            mapOperator = null
        )
        val operator = SchemaScopeMapOperator(
            root = "",
            argument = argument
        )
        val result = operator.collect()
        assertEquals(JsonObject(emptyMap()), result)
    }

    @Test
    fun `resolveMap returns empty map when initial element is JsonPrimitive`() {
        val source = JsonPrimitive("x")
        val argument = SchemaScopeArgument(
            element = source,
            moveOnRoot = false,
            mapOperator = null
        )
        val operator = SchemaScopeMapOperator(
            root = "",
            argument = argument
        )

        assertFalse(operator.contains("any"))
        assertNull(operator.read("any"))
        assertEquals(JsonObject(emptyMap()), operator.collect())
    }

    @Test
    fun `resolveMap returns empty map when initial element is JsonArray`() {
        val source = JsonArray(listOf(JsonPrimitive(1)))
        val argument = SchemaScopeArgument(
            element = source,
            moveOnRoot = false,
            mapOperator = null
        )
        val operator = SchemaScopeMapOperator(
            root = "",
            argument = argument
        )

        assertFalse(operator.contains("key"))
        assertNull(operator.read("key"))
        assertEquals(JsonObject(emptyMap()), operator.collect())
    }

    @Test
    fun `resolveMutableMap creates empty mutable map from JsonPrimitive`() {
        val source = JsonPrimitive("root")
        val argument = SchemaScopeArgument(
            element = source,
            moveOnRoot = false,
            mapOperator = null
        )
        val operator = SchemaScopeMapOperator(
            root = "",
            argument = argument
        )

        operator.write("k", JsonPrimitive("value"))
        assertEquals(JsonPrimitive("value"), operator.read("k"))
        assertTrue(operator.hasBeenChanged)
    }

    @Test
    fun `resolveMutableMap creates empty mutable map from JsonArray`() {
        val source = JsonArray(listOf(JsonPrimitive(1)))
        val argument = SchemaScopeArgument(
            element = source,
            moveOnRoot = false,
            mapOperator = null
        )
        val operator = SchemaScopeMapOperator(
            root = "",
            argument = argument
        )

        operator.write("alpha", JsonPrimitive(10))
        assertEquals(JsonPrimitive(10), operator.read("alpha"))
        assertTrue(operator.hasBeenChanged)
    }

    @Test
    fun `element returns JsonNull when moveOnRoot is true and element is JsonPrimitive`() {
        val source = JsonPrimitive("text")
        val argument = SchemaScopeArgument(
            element = source,
            moveOnRoot = true,
            mapOperator = null
        )
        val operator = SchemaScopeMapOperator(
            root = "ignored",
            argument = argument
        )
        val result = operator.element
        assertEquals(JsonNull, result)
    }

    @Test
    fun `element returns JsonNull when moveOnRoot is true and element is JsonArray`() {
        val source = JsonArray(listOf(JsonPrimitive(1)))
        val argument = SchemaScopeArgument(
            element = source,
            moveOnRoot = true,
            mapOperator = null
        )
        val operator = SchemaScopeMapOperator(
            root = "ignored",
            argument = argument
        )
        val result = operator.element
        assertEquals(JsonNull, result)
    }
}
