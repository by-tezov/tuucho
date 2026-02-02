package com.tezov.tuucho.core.domain.business._system.coroutine

import com.tezov.tuucho.core.domain.business.protocol.CoroutineExceptionMonitorProtocol
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CoroutineContextTest {
    class MonitorRecorder : CoroutineExceptionMonitorProtocol {
        val recordedContexts = mutableListOf<CoroutineExceptionMonitorProtocol.Context>()

        override suspend fun process(
            context: CoroutineExceptionMonitorProtocol.Context
        ) {
            recordedContexts.add(context)
        }
    }

    @Test
    fun `async captures exception and notifies monitor`() = runTest {
        val monitorRecorder = MonitorRecorder()
        val coroutineScope = CoroutineScope(
            name = "test-scope",
            dispatcher = StandardTestDispatcher(testScheduler),
            exceptionMonitor = monitorRecorder
        )

        val deferredResult = coroutineScope.async { error("boom") }

        assertFailsWith<IllegalStateException> {
            deferredResult.await()
        }

        assertEquals(1, monitorRecorder.recordedContexts.size)
        assertEquals("test-scope", monitorRecorder.recordedContexts.first().name)
    }

    @Test
    fun `async success does not notify monitor`() = runTest {
        val monitorRecorder = MonitorRecorder()
        val coroutineScope = CoroutineScope(
            name = "context",
            dispatcher = StandardTestDispatcher(testScheduler),
            exceptionMonitor = monitorRecorder
        )

        val deferredResult = coroutineScope.async { 123 }
        assertEquals(123, deferredResult.await())
        assertTrue(monitorRecorder.recordedContexts.isEmpty())
    }

    @Test
    fun `await executes block and returns value`() = runTest {
        val coroutineScope = CoroutineScope(
            name = "context",
            dispatcher = StandardTestDispatcher(testScheduler),
            exceptionMonitor = null
        )
        val value = coroutineScope.withContext { 42 }
        assertEquals(42, value)
    }

    @Test
    fun `async throw`() = runTest {
        val coroutineScope = CoroutineScope(
            name = "context",
            dispatcher = StandardTestDispatcher(testScheduler),
            exceptionMonitor = null
        )

        val deferredResult = coroutineScope.async { error("boom") }

        assertFailsWith<IllegalStateException> {
            deferredResult.await()
        }
    }
}
