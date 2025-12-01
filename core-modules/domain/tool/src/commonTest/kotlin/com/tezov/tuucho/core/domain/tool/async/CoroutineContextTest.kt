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
        override fun process(context: CoroutineExceptionMonitor.Context) {
            recordedContexts.add(context)
        }
    }

    @Test
    fun `async captures exception and notifies monitor`() = runTest {
        val monitorRecorder = MonitorRecorder()
        val coroutineContext = CoroutineContext(
            name = "test-scope",
            context = EmptyCoroutineContext,
            exceptionMonitor = monitorRecorder
        )

        val deferredResult = coroutineContext.async { error("boom") }

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
            name = "ctx",
            context = EmptyCoroutineContext,
            exceptionMonitor = monitorRecorder
        )

        val deferredResult = coroutineContext.async { 123 }
        assertEquals(123, deferredResult.await())
        assertTrue(monitorRecorder.recordedContexts.isEmpty())
    }

    @Test
    fun `await executes block and returns value`() = runTest {
        val coroutineContext = CoroutineContext(
            name = "ctx",
            context = EmptyCoroutineContext,
            exceptionMonitor = null
        )
        val value = coroutineContext.await { 42 }
        assertEquals(42, value)
    }
}
