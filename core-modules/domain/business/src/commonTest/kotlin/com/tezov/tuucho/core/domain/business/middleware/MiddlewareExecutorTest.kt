@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business.middleware

import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
    fun `execute with no middleware returns null`() = runTest {
        val result = sut.process(emptyList<MiddlewareProtocol<String, String>>(), "context")
        assertNull(result)
    }

    @Test
    fun `execute with single middleware runs process and returns its result`() = runTest {
        val middleware = mock<MiddlewareProtocol<String, String>>()

        everySuspend { middleware.process(any(), any()) } returns "value"

        val result = sut.process(listOf(middleware), "context")

        assertEquals("value", result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            middleware.process("context", any())
        }
    }

    @Test
    fun `execute with single middleware receives null next`() = runTest {
        val middleware = mock<MiddlewareProtocol<String, String>>()

        everySuspend { middleware.process(any(), any()) } calls { callArgs ->
            val receivedNext = callArgs.arg<MiddlewareProtocol.Next<String, String>?>(1)
            if (receivedNext == null) "ok-null" else "wrong"
        }

        val result = sut.process(listOf(middleware), "context")

        assertEquals("ok-null", result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            middleware.process("context", null)
        }
        verifyNoMoreCalls(middleware)
    }

    @Test
    fun `execute processes middlewares in order`() = runTest {
        val first = mock<MiddlewareProtocol<String, String>>()
        val second = mock<MiddlewareProtocol<String, String>>()

        everySuspend { first.process(any(), any()) } calls { callArgs ->
            val context = callArgs.arg<String>(0)
            val next = callArgs.arg<MiddlewareProtocol.Next<String, String>?>(1)
            val result = "first"
            val fromNext = next?.invoke("$context-from-$result")
            "$result-$context+$fromNext"
        }

        everySuspend { second.process(any(), any()) } calls { callArgs ->
            val context = callArgs.arg<String>(0)
            val next = callArgs.arg<MiddlewareProtocol.Next<String, String>?>(1)
            val result = "second"
            val fromNext = next?.invoke(context)
            "$result-$context+${fromNext ?: "terminal is null"}"
        }

        val result = sut.process(listOf(first, second), "context")

        assertEquals("first-context+second-context-from-first+terminal is null", result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            first.process("context", any())
            second.process("context-from-first", any())
        }
        verifyNoMoreCalls(first)
        verifyNoMoreCalls(second)
    }

    @Test
    fun `execute with three middlewares chained rewrite`() = runTest {
        val first = mock<MiddlewareProtocol<String, String>>()
        val second = mock<MiddlewareProtocol<String, String>>()
        val third = mock<MiddlewareProtocol<String, String>>()

        everySuspend { third.process(any(), any()) } calls { callArgs ->
            val context = callArgs.arg<String>(0)
            "third-$context"
        }

        everySuspend { second.process(any(), any()) } calls { callArgs ->
            val context = callArgs.arg<String>(0)
            val next = callArgs.arg<MiddlewareProtocol.Next<String, String>?>(1)
            val fromNext = next?.invoke("second-$context")
            "second-$context+$fromNext"
        }

        everySuspend { first.process(any(), any()) } calls { callArgs ->
            val context = callArgs.arg<String>(0)
            val next = callArgs.arg<MiddlewareProtocol.Next<String, String>?>(1)
            val fromNext = next?.invoke("first-$context")
            "first-$context+$fromNext"
        }

        val result = sut.process(listOf(first, second, third), "x")

        assertEquals("first-x+second-first-x+third-second-first-x", result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            first.process("x", any())
            second.process("first-x", any())
            third.process("second-first-x", any())
        }

        verifyNoMoreCalls(first)
        verifyNoMoreCalls(second)
        verifyNoMoreCalls(third)
    }

    @Test
    fun `execute stops early when next is not called`() = runTest {
        val first = mock<MiddlewareProtocol<String, String>>()
        val second = mock<MiddlewareProtocol<String, String>>()

        everySuspend { first.process(any(), any()) } calls { "stop" }

        val result = sut.process(listOf(first, second), "context")

        assertEquals("stop", result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            first.process("context", any())
        }

        verifyNoMoreCalls(first)
        verifyNoMoreCalls(second)
    }

    @Test
    fun `execute middleware returns null but still calls next`() = runTest {
        val first = mock<MiddlewareProtocol<String, String>>()
        val second = mock<MiddlewareProtocol<String, String>>()

        everySuspend { second.process(any(), any()) } returns "terminal"

        everySuspend { first.process(any(), any()) } calls { callArgs ->
            val context = callArgs.arg<String>(0)
            val next = callArgs.arg<MiddlewareProtocol.Next<String, String>?>(1)
            next?.invoke("$context-from-first")
            null
        }

        val result = sut.process(listOf(first, second), "context")

        assertEquals(null, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            first.process("context", any())
            second.process("context-from-first", any())
        }

        verifyNoMoreCalls(first)
        verifyNoMoreCalls(second)
    }
}
