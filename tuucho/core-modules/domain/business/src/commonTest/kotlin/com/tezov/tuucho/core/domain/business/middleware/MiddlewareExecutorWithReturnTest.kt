@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business.middleware

import com.tezov.tuucho.core.domain.business.mock.middlewareWithReturn.MockStringMiddlewareWithReturn
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocolWithReturn
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MiddlewareExecutorWithReturnTest {
    private lateinit var sut: MiddlewareExecutorProtocolWithReturn

    @BeforeTest
    fun setup() {
        sut = MiddlewareExecutorWithReturn()
    }

    @AfterTest
    fun tearDown() {
    }

    @Test
    fun `execute with no middleware returns nothing`() = runTest {
        val result = sut.process(emptyList<MiddlewareProtocolWithReturn<String, String>>(), "context").toList()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `execute with single middleware runs process and returns its result`() = runTest {
        val first = MockStringMiddlewareWithReturn("first")

        val result = sut.process(listOf(first), "context").toList()

        assertEquals(1, result.size)
        assertEquals("first", result.first())
    }

    @Test
    fun `execute processes two middlewares in order`() = runTest {
        val first = MockStringMiddlewareWithReturn("first")
        val second = MockStringMiddlewareWithReturn("second")

        val result = sut.run { process(listOf(first, second), "context") }.toList()

        assertEquals(listOf("first", "second"), result)
    }

    @Test
    fun `execute processes three middlewares in order`() = runTest {
        val first = MockStringMiddlewareWithReturn("first")
        val second = MockStringMiddlewareWithReturn("second")
        val third = MockStringMiddlewareWithReturn("third")

        val result = sut.run { process(listOf(first, third, second), "context") }.toList()

        assertEquals(listOf("first", "third", "second"), result)
    }

    @Test
    fun `execute processes stop when next is not called`() = runTest {
        val first = MockStringMiddlewareWithReturn("first")
        val second = MockStringMiddlewareWithReturn("second", callNext = false)
        val third = MockStringMiddlewareWithReturn("third")

        val result = sut.run { process(listOf(first, second, third), "context") }.toList()

        assertEquals(listOf("first", "second"), result)
    }

    @Test
    fun `execute processes can emit after`() = runTest {
        val first = MockStringMiddlewareWithReturn("first")
        val second = MockStringMiddlewareWithReturn("second", sendBefore = false)
        val third = MockStringMiddlewareWithReturn("third")

        val result = sut.run { process(listOf(first, second, third), "context") }.toList()

        assertEquals(listOf("first", "third", "second"), result)
    }
}
