@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business.middleware

import com.tezov.tuucho.core.domain.business.mock.MockStringMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MiddlewareExecutorTest {
    private lateinit var sut: MiddlewareExecutorProtocol

    @BeforeTest
    fun setup() {
        sut = MiddlewareExecutor()
    }

    @AfterTest
    fun tearDown() {
    }

    @Test
    fun `execute with no middleware returns nothing`() = runTest {
        val result = flow { sut.run { process(emptyList<MiddlewareProtocol<String, String>>(), "context") } }.toList()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `execute with single middleware runs process and returns its result`() = runTest {
        val middleware = MockStringMiddleware("first")

        val result = flow { sut.run { process(listOf(middleware), "context") } }.toList()

        assertEquals(1, result.size)
        assertEquals("first", result.first())
    }

    @Test
    fun `execute processes two middlewares in order`() = runTest {
        val first = MockStringMiddleware("first")
        val second = MockStringMiddleware("second")

        val result = flow { sut.run { process(listOf(first, second), "context") } }.toList()

        assertEquals(listOf("first", "second"), result)
    }

    @Test
    fun `execute processes three middlewares in order`() = runTest {
        val first = MockStringMiddleware("first")
        val second = MockStringMiddleware("second")
        val third = MockStringMiddleware("third")

        val result = flow { sut.run { process(listOf(first, third, second), "context") } }.toList()

        assertEquals(listOf("first", "third", "second"), result)
    }

    @Test
    fun `execute processes stop when next is not called`() = runTest {
        val first = MockStringMiddleware("first")
        val second = MockStringMiddleware("second", callNext = false)
        val third = MockStringMiddleware("third")

        val result = flow { sut.run { process(listOf(first, second, third), "context") } }.toList()

        assertEquals(listOf("first", "second"), result)
    }

    @Test
    fun `execute processes can emit after`() = runTest {
        val first = MockStringMiddleware("first")
        val second = MockStringMiddleware("second", emitBefore = false)
        val third = MockStringMiddleware("third")

        val result = flow { sut.run { process(listOf(first, second, third), "context") } }.toList()

        assertEquals(listOf("first", "third", "second"), result)
    }
}
