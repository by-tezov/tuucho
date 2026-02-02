@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business.middleware

import com.tezov.tuucho.core.domain.business.mock.middleware.MockStringMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

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
        sut.process(emptyList(), "context")
    }

    @Test
    fun `execute with single middleware runs process and returns its result`() = runTest {
        val first = MockStringMiddleware("first")

        sut.process(listOf(first), 1)

        assertEquals(1, first.contextEcho)
        assertEquals("first", first.commendEcho)
    }

    @Test
    fun `execute processes two middlewares in order`() = runTest {
        val first = MockStringMiddleware("first")
        val second = MockStringMiddleware("second")

        sut.process(listOf(first, second), 1)

        assertEquals(1, first.contextEcho)
        assertEquals("first", first.commendEcho)

        assertEquals(2, second.contextEcho)
        assertEquals("second", second.commendEcho)
    }

    @Test
    fun `execute processes three middlewares in order`() = runTest {
        val first = MockStringMiddleware("first")
        val second = MockStringMiddleware("second")
        val third = MockStringMiddleware("third")

        sut.process(listOf(first, third, second), 1)

        assertEquals(1, first.contextEcho)
        assertEquals("first", first.commendEcho)

        assertEquals(3, second.contextEcho)
        assertEquals("second", second.commendEcho)

        assertEquals(2, third.contextEcho)
        assertEquals("third", third.commendEcho)
    }

    @Test
    fun `execute processes stop when next is not called`() = runTest {
        val first = MockStringMiddleware("first")
        val second = MockStringMiddleware("second", callNext = false)
        val third = MockStringMiddleware("third")

        sut.process(listOf(first, second, third), 1)

        assertEquals(1, first.contextEcho)
        assertEquals("first", first.commendEcho)

        assertEquals(2, second.contextEcho)
        assertEquals("second", second.commendEcho)

        assertEquals(null, third.contextEcho)
        assertEquals(null, third.commendEcho)
    }
}
