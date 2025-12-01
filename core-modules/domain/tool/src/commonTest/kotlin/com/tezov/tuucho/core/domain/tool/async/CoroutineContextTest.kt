package com.tezov.tuucho.core.domain.tool.async

import kotlinx.coroutines.test.runTest
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CoroutineContextTest {
    class MonitorRecorder : CoroutineExceptionMonitor {
        val recordedContexts = mutableListOf<CoroutineExceptionMonitor.Context>()

        override fun process(
            context: CoroutineExceptionMonitor.Context
        ) {
            recordedContexts.add(context)
        }
    }

    class UncaughtExceptionHandlerRecorder : CoroutineUncaughtExceptionHandler {
        val recordedThrowables = mutableListOf<Throwable>()

        override fun process(throwable: Throwable): Throwable? {
            recordedThrowables.add(throwable)
            return null
        }
    }


    @Test
    fun `async captures exception and notifies monitor`() = runTest {
        val monitorRecorder = MonitorRecorder()
        val coroutineContext = CoroutineContext(
            name = "test-scope",
            context = EmptyCoroutineContext,
            exceptionMonitor = monitorRecorder,
            uncaughtExceptionHandler = null
        )

        val deferredResult = coroutineContext.async(false) { error("boom") }

        assertFailsWith<IllegalStateException> {
            deferredResult.await()
        }

        assertEquals(1, monitorRecorder.recordedContexts.size)
        assertEquals("test-scope", monitorRecorder.recordedContexts.first().name)
    }

    @Test
    fun `async success does not notify monitor`() = runTest {
        val monitorRecorder = MonitorRecorder()
        val coroutineContext = CoroutineContext(
            name = "context",
            context = EmptyCoroutineContext,
            exceptionMonitor = monitorRecorder,
            uncaughtExceptionHandler = null
        )

        val deferredResult = coroutineContext.async(false) { 123 }
        assertEquals(123, deferredResult.await())
        assertTrue(monitorRecorder.recordedContexts.isEmpty())
    }

    @Test
    fun `await executes block and returns value`() = runTest {
        val coroutineContext = CoroutineContext(
            name = "context",
            context = EmptyCoroutineContext,
            exceptionMonitor = null,
            uncaughtExceptionHandler = null
        )
        val value = coroutineContext.await { 42 }
        assertEquals(42, value)
    }

    @Test
    fun `async with throwOnFailure delegates exception to uncaught handler`() = runTest {
        val handlerRecorder = UncaughtExceptionHandlerRecorder()
        val coroutineContext = CoroutineContext(
            name = "context",
            context = EmptyCoroutineContext,
            exceptionMonitor = null,
            uncaughtExceptionHandler = handlerRecorder
        )

        val deferredResult = coroutineContext.async(true) { error("boom") }

        assertFailsWith<IllegalStateException> {
            deferredResult.await()
        }

        assertEquals(1, handlerRecorder.recordedThrowables.size)
        assertTrue(handlerRecorder.recordedThrowables.first() is IllegalStateException)
    }

    @Test
    fun `async without throwOnFailure does not delegate exception to uncaught handler`() = runTest {
        val handlerRecorder = UncaughtExceptionHandlerRecorder()
        val coroutineContext = CoroutineContext(
            name = "context",
            context = EmptyCoroutineContext,
            exceptionMonitor = null,
            uncaughtExceptionHandler = handlerRecorder
        )

        val deferredResult = coroutineContext.async(false) { error("boom") }

        assertFailsWith<IllegalStateException> {
            deferredResult.await()
        }

        assertTrue(handlerRecorder.recordedThrowables.isEmpty())
    }
}
