@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business.jsonSchema._system

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifyNoMoreCalls
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SchemaScopeTest {
    private lateinit var mapOperator: OpenSchemaScope.MapOperator

    private class TestScope(
        argument: SchemaScopeArgument
    ) : OpenSchemaScope<TestScope>(argument)

    @BeforeTest
    fun setup() {
        mapOperator = mock()
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(mapOperator)
    }

    @Test
    fun `element returns operator element`() {
        every { mapOperator.element } returns JsonPrimitive("value")
        val scope = TestScope(
            SchemaScopeArgument(
                element = JsonPrimitive("ignored"),
                moveOnRoot = false,
                mapOperator = mapOperator
            )
        )
        val result = scope.element
        assertEquals(JsonPrimitive("value"), result)
        verify { mapOperator.element }
    }

    @Test
    fun `contains delegates to mapOperator`() {
        every { mapOperator.contains("key") } returns true
        val scope = TestScope(
            SchemaScopeArgument(
                element = JsonObject(emptyMap()),
                moveOnRoot = false,
                mapOperator = mapOperator
            )
        )
        val result = scope.contains("key")
        assertTrue(result)
        verify { mapOperator.contains("key") }
    }

    @Test
    fun `get delegates read to mapOperator`() {
        every { mapOperator.read("alpha") } returns JsonPrimitive("v")
        val scope = TestScope(
            SchemaScopeArgument(
                element = JsonObject(emptyMap()),
                moveOnRoot = false,
                mapOperator = mapOperator
            )
        )
        val result = scope["alpha"]
        assertEquals(JsonPrimitive("v"), result)
        verify { mapOperator.read("alpha") }
    }

    @Test
    fun `get returns null when read returns null`() {
        every { mapOperator.read("alpha") } returns null
        val scope = TestScope(
            SchemaScopeArgument(
                element = JsonObject(emptyMap()),
                moveOnRoot = false,
                mapOperator = mapOperator
            )
        )
        val result = scope["alpha"]
        assertNull(result)
        verify { mapOperator.read("alpha") }
    }

    @Test
    fun `set delegates to mapOperator write`() {
        every { mapOperator.write("k", JsonPrimitive(1)) } returns Unit
        val scope = TestScope(
            SchemaScopeArgument(
                element = JsonObject(emptyMap()),
                moveOnRoot = false,
                mapOperator = mapOperator
            )
        )
        scope["k"] = JsonPrimitive(1)
        verify { mapOperator.write("k", JsonPrimitive(1)) }
    }

    @Test
    fun `keys returns json object keys`() {
        every { mapOperator.element } returns JsonObject(
            mapOf(
                "a" to JsonPrimitive(1),
                "b" to JsonPrimitive(2)
            )
        )
        val scope = TestScope(
            SchemaScopeArgument(
                element = JsonPrimitive("ignored"),
                moveOnRoot = false,
                mapOperator = mapOperator
            )
        )
        val keys = scope.keys()
        assertEquals(setOf("a", "b"), keys)
        verify { mapOperator.element }
    }

    @Test
    fun `keys returns empty set when element is not json object`() {
        every { mapOperator.element } returns JsonPrimitive("x")
        val scope = TestScope(
            SchemaScopeArgument(
                element = JsonPrimitive("ignored"),
                moveOnRoot = false,
                mapOperator = mapOperator
            )
        )
        val keys = scope.keys()
        assertTrue(keys.isEmpty())
        verify { mapOperator.element }
    }

    @Test
    fun `remove delegates to mapOperator`() {
        every { mapOperator.remove("rm") } returns Unit
        val scope = TestScope(
            SchemaScopeArgument(
                element = JsonObject(emptyMap()),
                moveOnRoot = false,
                mapOperator = mapOperator
            )
        )
        scope.remove("rm")
        verify { mapOperator.remove("rm") }
    }

    @Test
    fun `withScope uses same mapOperator and wraps element`() {
        every { mapOperator.element } returns JsonObject(mapOf("a" to JsonPrimitive(1)))
        val scope = TestScope(
            SchemaScopeArgument(
                element = JsonPrimitive("ignored"),
                moveOnRoot = false,
                mapOperator = mapOperator
            )
        )
        val next = scope.withScope { TestScope(it) }
        assertEquals(JsonObject(mapOf("a" to JsonPrimitive(1))), next.element)
        verify { mapOperator.element }
    }

    @Test
    fun `onScope resolves element to JsonNull when root is empty`() {
        every { mapOperator.element } returns JsonObject(mapOf("root" to JsonPrimitive("val")))
        val scope = TestScope(
            SchemaScopeArgument(
                element = JsonPrimitive("ignored"),
                moveOnRoot = false,
                mapOperator = mapOperator
            )
        )
        val next = scope.onScope { TestScope(it) }
        assertEquals(JsonNull, next.element)
        verify { mapOperator.element }
    }

    @Test
    fun `collect delegates to mapOperator`() {
        val collected = JsonObject(mapOf("x" to JsonPrimitive(9)))
        every { mapOperator.collect() } returns collected
        val scope = TestScope(
            SchemaScopeArgument(
                element = JsonNull,
                moveOnRoot = false,
                mapOperator = mapOperator
            )
        )
        val result = scope.collect()
        assertEquals(collected, result)
        verify { mapOperator.collect() }
    }

    @Test
    fun `collectChangedOrNull returns null when not changed`() {
        every { mapOperator.hasBeenChanged } returns false
        val scope = TestScope(
            SchemaScopeArgument(
                element = JsonNull,
                moveOnRoot = false,
                mapOperator = mapOperator
            )
        )
        val result = scope.collectChangedOrNull()
        assertNull(result)
        verify { mapOperator.hasBeenChanged }
    }

    @Test
    fun `collectChangedOrNull returns collected map when changed`() {
        val collected = JsonObject(mapOf("y" to JsonPrimitive(2)))
        every { mapOperator.hasBeenChanged } returns true
        every { mapOperator.collect() } returns collected
        val scope = TestScope(
            SchemaScopeArgument(
                element = JsonNull,
                moveOnRoot = false,
                mapOperator = mapOperator
            )
        )
        val result = scope.collectChangedOrNull()
        assertEquals(collected, result)
        verify { mapOperator.hasBeenChanged }
        verify { mapOperator.collect() }
    }

    @Test
    fun `toString returns initial and current`() {
        every { mapOperator.element } returns JsonPrimitive("i")
        every { mapOperator.collect() } returns JsonObject(mapOf("k" to JsonPrimitive(1)))
        val scope = TestScope(
            SchemaScopeArgument(
                element = JsonNull,
                moveOnRoot = false,
                mapOperator = mapOperator
            )
        )
        val result = scope.toString()
        assertTrue(result.contains("initial=\"i\""))
        assertTrue(result.contains("current={\"k\":1}"))
        verify { mapOperator.element }
        verify { mapOperator.collect() }
    }
}
