package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockMonitor.Event
import com.tezov.tuucho.core.domain.business.mock.CoroutineTestScope
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLock
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import dev.mokkery.answering.returns
import dev.mokkery.answering.returnsBy
import dev.mokkery.every
import dev.mokkery.matcher.MokkeryMatcherScope
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matches
import dev.mokkery.mock
import dev.mokkery.resetCalls
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InteractionLockStackTest {
    private val coroutineTestScope = CoroutineTestScope()
    private lateinit var generator: InteractionLockGenerator
    private lateinit var monitor: InteractionLockMonitor
    private lateinit var sut: InteractionLockStack
    private lateinit var sutMonitored: InteractionLockStack
    private lateinit var counters: MutableMap<InteractionLockType, Int>

    private val requester = "test"

    private fun input(
        type: InteractionLockType
    ) = InteractionLockGenerator.Input(owner = requester, type = type)

    private fun id(
        type: InteractionLockType,
        count: Int
    ) = "${type}_$count"

    private fun stub(
        type: InteractionLockType
    ) {
        every { generator.generate(input(type)) } returnsBy {
            val count = counters.getValue(type).also { counters[type] = it + 1 }
            InteractionLock(owner = requester, id = id(type, count), type = type)
        }
    }

    private fun MokkeryMatcherScope.matches(
        type: InteractionLockType
    ) = matches<InteractionLockGenerator.Input> {
        it.type == type
    }

    @BeforeTest
    fun setup() {
        counters = mutableMapOf<InteractionLockType, Int>().withDefault { 1 }

        generator = mock()
        listOf(InteractionLockType.Screen, InteractionLockType.Navigation).forEach { stub(it) }

        monitor = mock()
        every { monitor.process(any()) } returns Unit

        coroutineTestScope.setup()
        sut = InteractionLockStack(
            coroutineScopes = coroutineTestScope.mock,
            lockGenerator = generator,
            interactionLockMonitor = null
        )
        sutMonitored = InteractionLockStack(
            coroutineScopes = coroutineTestScope.mock,
            lockGenerator = generator,
            interactionLockMonitor = monitor
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestScope.verifyNoMoreCalls()
        verifyNoMoreCalls(
            generator,
            monitor
        )
    }

    @Test
    fun `tryAcquire empty returns empty`() = coroutineTestScope.run {
        val result = sut.tryAcquire(requester, emptyList())
        assertEquals(emptyList(), result)

        verifySuspend {
            coroutineTestScope.mock.default.await<Any>(any())
        }
    }

    @Test
    fun `acquire one lock`() = coroutineTestScope.run {
        val lock = sut.acquire(requester, InteractionLockType.Screen)

        assertEquals(InteractionLockType.Screen, lock.type)
        assertEquals(id(InteractionLockType.Screen, 1), lock.id)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.await<Any>(any())
            generator.generate(matches(InteractionLockType.Screen))
        }
    }

    @Test
    fun `acquire release reacquire`() = coroutineTestScope.run {
        val lock = sut.acquire(requester, InteractionLockType.Screen)
        sut.release(requester, lock)
        val reacquired = sut.tryAcquire(requester, InteractionLockType.Screen)

        assertNotNull(reacquired)
        assertEquals(InteractionLockType.Screen, reacquired.type)
        assertEquals(id(InteractionLockType.Screen, 2), reacquired.id)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            // acquire
            coroutineTestScope.mock.default.await<Any>(any())
            generator.generate(matches(InteractionLockType.Screen))
            // release
            coroutineTestScope.mock.default.await<Any>(any())
            coroutineTestScope.mock.io.async<Any>(any())
            // reacquired
            coroutineTestScope.mock.default.await<Any>(any())
            generator.generate(matches(InteractionLockType.Screen))
        }
    }

    @Test
    fun `acquire multiple`() = coroutineTestScope.run {
        val types = listOf(InteractionLockType.Screen, InteractionLockType.Navigation)
        val locks = sut.acquire(requester, types)
        assertEquals(types, locks.map { it.type })

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.await<Any>(any())
            generator.generate(matches(InteractionLockType.Screen))
            generator.generate(matches(InteractionLockType.Navigation))
        }
    }

    @Test
    fun `tryAcquire fails when held`() = coroutineTestScope.run {
        sut.acquire(requester, InteractionLockType.Navigation)
        coroutineTestScope.resetCalls()
        resetCalls(generator)

        val result = sut.tryAcquire(requester, InteractionLockType.Navigation)

        assertNull(result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.await<Any>(any())
        }
    }

    @Test
    fun `tryAcquire multi fails when one held`() = coroutineTestScope.run {
        sut.acquire(requester, InteractionLockType.Navigation)
        coroutineTestScope.resetCalls()
        resetCalls(generator)

        val result = sut.tryAcquire(requester, listOf(InteractionLockType.Screen, InteractionLockType.Navigation))

        assertNull(result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.await<Any>(any())
        }
    }

    @Test
    fun `atomic multi acquire`() = coroutineTestScope.run {
        sut.acquire(requester, listOf(InteractionLockType.Screen, InteractionLockType.Navigation))

        val resultScreen = sut.tryAcquire(requester, InteractionLockType.Screen)
        val resultNavigation = sut.tryAcquire(requester, InteractionLockType.Navigation)

        assertNull(resultScreen)
        assertNull(resultNavigation)

        coroutineTestScope.resetCalls()
        resetCalls(generator)
    }

    @Test
    fun `waiter resumes after release`() = coroutineTestScope.run {
        val first = sut.acquire(requester, InteractionLockType.Screen)
        val deferred = async { sut.acquire(requester, InteractionLockType.Screen) }
        delay(5)

        assertTrue(deferred.isActive)

        sut.release(requester, first)
        val second = deferred.await()

        assertEquals(id(InteractionLockType.Screen, 2), second.id)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            // acquire first
            coroutineTestScope.mock.default.await<Any>(any())
            generator.generate(matches(InteractionLockType.Screen))
            // acquire async
            coroutineTestScope.mock.default.await<Any>(any())
            coroutineTestScope.mock.io.await<Any>(any())
            // release
            coroutineTestScope.mock.default.await<Any>(any())
            coroutineTestScope.mock.io.async<Any>(any())
            // acquire second
            coroutineTestScope.mock.default.await<Any>(any())
            generator.generate(matches(InteractionLockType.Screen))
        }
    }

    @Test
    fun `waiters resume FIFO`() = coroutineTestScope.run {
        val order = mutableListOf<String>()
        val initialLock = sut.acquire(requester, InteractionLockType.Screen)
        val waiter1 = async { sut.acquire(requester, InteractionLockType.Screen).also { order += "1" } }
        val waiter2 = async { sut.acquire(requester, InteractionLockType.Screen).also { order += "2" } }
        delay(5)
        sut.release(requester, initialLock)
        val waiter1Lock = waiter1.await()
        sut.release(requester, waiter1Lock)
        waiter2.await()
        assertEquals(listOf("1", "2"), order)

        coroutineTestScope.resetCalls()
        resetCalls(generator)
    }

    @Test
    fun `selective wakeup`() = coroutineTestScope.run {
        val both = listOf(InteractionLockType.Screen, InteractionLockType.Navigation)
        val screenHeldLock = sut.acquire(requester, InteractionLockType.Screen)
        val waiterBoth = async { sut.acquire(requester, both) }
        val waiterNavigation = async { sut.acquire(requester, InteractionLockType.Navigation) }
        delay(5)
        sut.release(requester, screenHeldLock)
        delay(5)
        val waiterNavigationLock = waiterNavigation.await()
        sut.release(requester, waiterNavigationLock)
        delay(5)
        val result = waiterBoth.await()

        assertEquals(both, result.map { it.type })

        coroutineTestScope.resetCalls()
        resetCalls(generator)
    }

    @Test
    fun `ignore non releasable`() = coroutineTestScope.run {
        val unreleasable = InteractionLock(
            owner = requester,
            id = id(InteractionLockType.Screen, counters.getValue(InteractionLockType.Screen)),
            type = InteractionLockType.Screen,
            canBeReleased = false
        )

        every { generator.generate(input(InteractionLockType.Screen)) } returns unreleasable

        val acquired = sut.acquire(requester, InteractionLockType.Screen)
        val waiter = async { sut.acquire(requester, InteractionLockType.Screen) }
        delay(5)
        sut.release(requester, acquired)
        delay(5)
        assertTrue(waiter.isActive)
        waiter.cancel()

        coroutineTestScope.resetCalls()
        resetCalls(generator)
    }

    @Test
    fun `release wakes one waiter`() = coroutineTestScope.run {
        val lock = sut.acquire(requester, InteractionLockType.Screen)
        val waiter1 = async { sut.acquire(requester, InteractionLockType.Screen) }
        val waiter2 = async { sut.acquire(requester, InteractionLockType.Screen) }
        delay(5)
        sut.release(requester, lock)

        val result = waiter1.await()
        assertNotNull(result)

        assertTrue(waiter2.isActive)
        waiter2.cancel()

        coroutineTestScope.resetCalls()
        resetCalls(generator)
    }

    @Test
    fun `isValid true when current`() = coroutineTestScope.run {
        val lock = sut.acquire(requester, InteractionLockType.Screen)
        coroutineTestScope.resetCalls()
        resetCalls(generator)

        val result = sut.isValid(lock)

        assertTrue(result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.await<Any>(any())
        }
    }

    @Test
    fun `isValid false after release`() = coroutineTestScope.run {
        val lock = sut.acquire(requester, InteractionLockType.Screen)
        sut.release(requester, lock)

        val result = sut.isValid(lock)

        assertFalse(result)

        coroutineTestScope.resetCalls()
        resetCalls(generator)
    }

    @Test
    fun `isValid false for different id`() = coroutineTestScope.run {
        val original = sut.acquire(requester, InteractionLockType.Screen)
        val fake = InteractionLock(owner = requester, id = "x", type = InteractionLockType.Screen)

        assertFalse(sut.isValid(fake))
        assertTrue(sut.isValid(original))

        coroutineTestScope.resetCalls()
        resetCalls(generator)
    }

    @Test
    fun `isAllValid true when all current`() = coroutineTestScope.run {
        val locks = sut.acquire(requester, listOf(InteractionLockType.Screen, InteractionLockType.Navigation))

        val result = sut.isAllValid(locks)

        assertTrue(result)

        coroutineTestScope.resetCalls()
        resetCalls(generator)
    }

    @Test
    fun `isAllValid false when one invalid`() = coroutineTestScope.run {
        val locks = sut.acquire(requester, listOf(InteractionLockType.Screen, InteractionLockType.Navigation))
        sut.release(requester, locks.first())

        val result = sut.isAllValid(locks)

        assertFalse(result)

        coroutineTestScope.resetCalls()
        resetCalls(generator)
    }

    @Test
    fun `monitor acquired`() = coroutineTestScope.run {
        sutMonitored.acquire(requester, InteractionLockType.Screen)
        coroutineTestScope.resetCalls()
        resetCalls(generator)

        verify(VerifyMode.exhaustiveOrder) {
            monitor.process(
                InteractionLockMonitor.Context(
                    event = Event.Acquired,
                    requester = listOf(requester),
                    lockTypes = listOf(InteractionLockType.Screen)
                )
            )
        }
    }

    @Test
    fun `monitor released`() = coroutineTestScope.run {
        val lock = sutMonitored.acquire(requester, InteractionLockType.Screen)
        resetCalls(monitor)
        sutMonitored.release(requester, lock)
        coroutineTestScope.resetCalls()
        resetCalls(generator)

        verify(VerifyMode.exhaustiveOrder) {
            monitor.process(
                InteractionLockMonitor.Context(
                    event = Event.Released,
                    requester = listOf(requester),
                    lockTypes = listOf(InteractionLockType.Screen)
                )
            )
        }
    }

    @Test
    fun `monitor tryAcquire`() = coroutineTestScope.run {
        sutMonitored.tryAcquire(requester, InteractionLockType.Screen)
        coroutineTestScope.resetCalls()
        resetCalls(generator)

        verify(VerifyMode.exhaustiveOrder) {
            monitor.process(
                InteractionLockMonitor.Context(
                    event = Event.AcquireFromTry,
                    requester = listOf(requester),
                    lockTypes = listOf(InteractionLockType.Screen)
                )
            )
        }
    }

    @Test
    fun `monitor wait and try again`() = coroutineTestScope.run {
        val first = sutMonitored.acquire(requester, InteractionLockType.Screen)
        val waiter = async { sutMonitored.acquire(requester, InteractionLockType.Screen) }
        delay(5)
        sutMonitored.release(requester, first)
        waiter.await()
        coroutineTestScope.resetCalls()
        resetCalls(generator)

        verify(VerifyMode.exhaustiveOrder) {
            monitor.process(
                InteractionLockMonitor.Context(
                    event = Event.Acquired,
                    requester = listOf(requester),
                    lockTypes = listOf(InteractionLockType.Screen)
                )
            )
            monitor.process(
                InteractionLockMonitor.Context(
                    event = Event.WaitToAcquire,
                    requester = listOf(requester),
                    lockTypes = listOf(InteractionLockType.Screen)
                )
            )
            monitor.process(
                InteractionLockMonitor.Context(
                    event = Event.Released,
                    requester = listOf(requester),
                    lockTypes = listOf(InteractionLockType.Screen)
                )
            )
            monitor.process(
                InteractionLockMonitor.Context(
                    event = Event.TryAcquireAgain,
                    requester = listOf(requester),
                    lockTypes = listOf(InteractionLockType.Screen)
                )
            )
            monitor.process(
                InteractionLockMonitor.Context(
                    event = Event.Acquired,
                    requester = listOf(requester),
                    lockTypes = listOf(InteractionLockType.Screen)
                )
            )
        }
    }

    @Test
    fun `monitor cannot release`() = coroutineTestScope.run {
        val unreleasable = InteractionLock(
            owner = requester,
            id = "x",
            type = InteractionLockType.Screen,
            canBeReleased = false
        )
        every { generator.generate(input(InteractionLockType.Screen)) } returns unreleasable
        val lock = sutMonitored.acquire(requester, InteractionLockType.Screen)
        resetCalls(monitor)
        sutMonitored.release(requester, lock)
        coroutineTestScope.resetCalls()
        resetCalls(generator)

        verify(VerifyMode.exhaustiveOrder) {
            monitor.process(
                InteractionLockMonitor.Context(
                    event = Event.CanNotBeReleased,
                    requester = listOf(requester),
                    lockTypes = listOf(InteractionLockType.Screen)
                )
            )
        }
    }
}
