package com.tezov.tuucho.core.domain.tool.async

import com.tezov.tuucho.core.domain.tool.async.ExtensionFlow.collectForever
import com.tezov.tuucho.core.domain.tool.async.ExtensionFlow.collectOnce
import com.tezov.tuucho.core.domain.tool.async.ExtensionFlow.collectUntil
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ExtensionFlowTest {
    @Test
    fun `collectOnce emits first value only`() = runTest {
        var receivedValue = 0
        flowOf(1, 2, 3).collectOnce { receivedValue = it }
        assertEquals(1, receivedValue)
    }

    @Test
    fun `collectForever collects all emitted values`() = runTest {
        val sharedFlow = MutableSharedFlow<Int>()
        val collectedValues = mutableListOf<Int>()

        val collectorJob = launch {
            sharedFlow.collectForever {
                collectedValues.add(it)
                if (collectedValues.size == 3) this.cancel()
            }
        }

        yield()
        sharedFlow.emit(10)
        sharedFlow.emit(20)
        sharedFlow.emit(30)
        yield()

        collectorJob.cancelAndJoin()
        assertEquals(listOf(10, 20, 30), collectedValues)
    }

    @Test
    fun `collectUntil stops when predicate returns true`() = runTest {
        val sharedFlow = MutableSharedFlow<Int>()
        val collectedValues = mutableListOf<Int>()

        val collectorJob = launch {
            sharedFlow.collectUntil {
                collectedValues.add(it)
                it == 2
            }
        }

        yield()
        sharedFlow.emit(1)
        sharedFlow.emit(2)
        sharedFlow.emit(3)
        yield()

        collectorJob.cancelAndJoin()
        assertEquals(listOf(1, 2), collectedValues)
    }

    @Test
    fun `collectForever on empty flow does not call block`() = runTest {
        var called = false
        flowOf<Int>().collectForever { called = true }
        assertFalse(called)
    }

    @Test
    fun `collectUntil on empty flow does not call block`() = runTest {
        var called = false
        flowOf<Int>().collectUntil { called = true; false }
        assertFalse(called)
    }

    @Test
    fun `collectUntil stops at first true predicate`() = runTest {
        val values = listOf(1, 2, 3)
        val collected = mutableListOf<Int>()
        flowOf(*values.toTypedArray()).collectUntil {
            collected.add(it)
            it == 2
        }
        assertEquals(listOf(1, 2), collected)
    }

    @Test
    fun `collectUntil never true predicate collects all until flow ends`() = runTest {
        val values = listOf(1, 2, 3)
        val collected = mutableListOf<Int>()
        flowOf(*values.toTypedArray()).collectUntil {
            collected.add(it)
            false
        }
        assertEquals(values, collected)
    }

}
