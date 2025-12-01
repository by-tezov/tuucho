package com.tezov.tuucho.core.domain.tool.async

import com.tezov.tuucho.core.domain.tool.async.Notifier.Emitter
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertEquals

class NotifierTest {

    @Test
    fun `tryEmit sends value to once collector`() = runTest {
        val emitter = Emitter<Int>()
        var receivedValue = 0

        val collectorJob = launch {
            emitter.createCollector.once { receivedValue = it }
        }

        yield()
        emitter.tryEmit(7)
        yield()

        collectorJob.cancelAndJoin()
        assertEquals(7, receivedValue)
    }

    @Test
    fun `emit sends values until predicate matches`() = runTest {
        val emitter = Emitter<Int>()
        val collectedValues = mutableListOf<Int>()

        val collectorJob = launch {
            emitter.createCollector.until {
                collectedValues.add(it)
                it == 3
            }
        }

        yield()
        emitter.emit(1)
        emitter.emit(2)
        emitter.emit(3)
        emitter.emit(4)
        yield()

        collectorJob.cancelAndJoin()
        assertEquals(listOf(1, 2, 3), collectedValues)
    }

    @Test
    fun `filter emits only matching values`() = runTest {
        val emitter = Emitter<Int>()
        val collectedValues = mutableListOf<Int>()

        val collectorJob = launch {
            emitter.createCollector
                .filter { it % 2 == 0 }
                .forever {
                    collectedValues.add(it)
                    if (collectedValues.size == 2) this.cancel()
                }
        }

        yield()
        emitter.emit(1)
        emitter.emit(2)
        emitter.emit(3)
        emitter.emit(4)
        emitter.emit(5)
        yield()

        collectorJob.cancelAndJoin()
        assertEquals(listOf(2, 4), collectedValues)
    }
}
