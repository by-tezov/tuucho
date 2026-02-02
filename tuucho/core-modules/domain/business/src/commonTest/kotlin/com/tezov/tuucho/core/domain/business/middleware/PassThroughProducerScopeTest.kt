package com.tezov.tuucho.core.domain.business.middleware

import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PassThroughProducerScopeTest {

    private lateinit var sut: PassThroughProducerScope<Int>

    @Test
    fun `send should forward element unchanged if onSendIntent returns same element`() = runTest {
        val results = mutableListOf<Int>()

        val flow = channelFlow {
            sut = PassThroughProducerScope(this) { it }
            sut.send(1)
            sut.send(2)
            sut.send(3)
        }
        flow.toList(results)

        assertEquals(listOf(1, 2, 3), results)
    }

    @Test
    fun `send should apply onSendIntent before forwarding in channelFlow`() = runTest {
        val results = mutableListOf<Int>()

        val flow = channelFlow {
            sut = PassThroughProducerScope(this) { it * 2 }
            sut.send(3)
            sut.send(7)
            sut.send(5)
        }

        flow.toList(results)

        assertEquals(listOf(6, 14, 10), results)
    }
}
