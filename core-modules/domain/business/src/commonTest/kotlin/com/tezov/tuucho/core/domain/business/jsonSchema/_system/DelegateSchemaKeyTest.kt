@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business.jsonSchema._system

import com.tezov.tuucho.core.domain.business.exception.DomainException
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifyNoMoreCalls
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class DelegateSchemaKeyTest {

    private lateinit var mapOperator: DelegateSchemaKey.MapOperator

    private class TestContainer(
        mapOperator: DelegateSchemaKey.MapOperator,
        type: kotlin.reflect.KClass<*>
    ) {
        var value: Any? by DelegateSchemaKey(mapOperator, type)
    }

    @BeforeTest
    fun setup() {
        mapOperator = mock()
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(mapOperator)
    }

    @Test
    fun `get returns json element when type is JsonElement`() {
        val jsonValue = JsonPrimitive("e")
        every { mapOperator.read("value") } returns jsonValue
        val container = TestContainer(mapOperator, JsonElement::class)
        val resultValue = container.value as JsonElement
        assertEquals(jsonValue, resultValue)
        verify { mapOperator.read("value") }
    }

    @Test
    fun `get returns json object when type is JsonObject`() {
        val jsonValue = JsonObject(mapOf("k" to JsonPrimitive("v")))
        every { mapOperator.read("value") } returns jsonValue
        val container = TestContainer(mapOperator, JsonObject::class)
        val resultValue = container.value as JsonElement
        assertEquals(jsonValue, resultValue)
        verify { mapOperator.read("value") }
    }

    @Test
    fun `get returns json primitive when type is JsonPrimitive`() {
        val jsonValue = JsonPrimitive("p")
        every { mapOperator.read("value") } returns jsonValue
        val container = TestContainer(mapOperator, JsonPrimitive::class)
        val resultValue = container.value as JsonElement
        assertEquals(jsonValue, resultValue)
        verify { mapOperator.read("value") }
    }

    @Test
    fun `get returns json array when type is JsonArray`() {
        val jsonValue = JsonArray(listOf(JsonPrimitive(1)))
        every { mapOperator.read("value") } returns jsonValue
        val container = TestContainer(mapOperator, JsonArray::class)
        val resultValue = container.value as JsonElement
        assertEquals(jsonValue, resultValue)
        verify { mapOperator.read("value") }
    }

    @Test
    fun `get returns json null when type is JsonNull`() {
        val jsonValue = JsonNull
        every { mapOperator.read("value") } returns jsonValue
        val container = TestContainer(mapOperator, JsonNull::class)
        val resultValue = container.value as JsonElement
        assertEquals(jsonValue, resultValue)
        verify { mapOperator.read("value") }
    }

    @Test
    fun `set writes json element when type is JsonElement`() {
        val jsonValue = JsonPrimitive("e")
        every { mapOperator.write("value", jsonValue) } returns Unit
        val container = TestContainer(mapOperator, JsonElement::class)
        container.value = jsonValue
        verify { mapOperator.write("value", jsonValue) }
    }

    @Test
    fun `set writes json object when type is JsonObject`() {
        val jsonValue = JsonObject(mapOf("a" to JsonPrimitive("b")))
        every { mapOperator.write("value", jsonValue) } returns Unit
        val container = TestContainer(mapOperator, JsonObject::class)
        container.value = jsonValue
        verify { mapOperator.write("value", jsonValue) }
    }

    @Test
    fun `set writes json primitive when type is JsonPrimitive`() {
        val jsonValue = JsonPrimitive("x")
        every { mapOperator.write("value", jsonValue) } returns Unit
        val container = TestContainer(mapOperator, JsonPrimitive::class)
        container.value = jsonValue
        verify { mapOperator.write("value", jsonValue) }
    }

    @Test
    fun `set writes json array when type is JsonArray`() {
        val jsonValue = JsonArray(listOf(JsonPrimitive("a")))
        every { mapOperator.write("value", jsonValue) } returns Unit
        val container = TestContainer(mapOperator, JsonArray::class)
        container.value = jsonValue
        verify { mapOperator.write("value", jsonValue) }
    }

    @Test
    fun `set writes json null when type is JsonNull`() {
        val jsonValue = JsonNull
        every { mapOperator.write("value", jsonValue) } returns Unit
        val container = TestContainer(mapOperator, JsonNull::class)
        container.value = jsonValue
        verify { mapOperator.write("value", jsonValue) }
    }

    @Test
    fun `get returns string when type is String`() {
        val jsonValue = JsonPrimitive("str")
        every { mapOperator.read("value") } returns jsonValue
        val container = TestContainer(mapOperator, String::class)
        val resultValue = container.value as String
        assertEquals("str", resultValue)
        verify { mapOperator.read("value") }
    }

    @Test
    fun `set writes string when type is String`() {
        every { mapOperator.write("value", JsonPrimitive("str")) } returns Unit
        val container = TestContainer(mapOperator, String::class)
        container.value = "str"
        verify { mapOperator.write("value", JsonPrimitive("str")) }
    }

    @Test
    fun `get returns boolean when type is Boolean`() {
        val jsonValue = JsonPrimitive(true)
        every { mapOperator.read("value") } returns jsonValue
        val container = TestContainer(mapOperator, Boolean::class)
        val resultValue = container.value as Boolean
        assertEquals(true, resultValue)
        verify { mapOperator.read("value") }
    }

    @Test
    fun `set writes boolean when type is Boolean`() {
        every { mapOperator.write("value", JsonPrimitive(false)) } returns Unit
        val container = TestContainer(mapOperator, Boolean::class)
        container.value = false
        verify { mapOperator.write("value", JsonPrimitive(false)) }
    }

    @Test
    fun `get returns null when map operator returns null`() {
        every { mapOperator.read("value") } returns null
        val container = TestContainer(mapOperator, String::class)
        val resultValue = container.value
        assertNull(resultValue)
        verify { mapOperator.read("value") }
    }

    @Test
    fun `set writes JsonNull when value is null`() {
        every { mapOperator.write("value", JsonNull) } returns Unit
        val container = TestContainer(mapOperator, String::class)
        container.value = null
        verify { mapOperator.write("value", JsonNull) }
    }

    @Test
    fun `get throws when type is unknown`() {
        val jsonValue = JsonPrimitive("ignored")
        every { mapOperator.read("value") } returns jsonValue
        val container = TestContainer(mapOperator, Int::class)
        assertFailsWith<DomainException.Default> {
            container.value
        }
        verify { mapOperator.read("value") }
    }

    @Test
    fun `set throws when type is unknown`() {
        val container = TestContainer(mapOperator, Int::class)
        assertFailsWith<DomainException.Default> {
            container.value = 7
        }
    }
}
