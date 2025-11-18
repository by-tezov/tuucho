package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockGenerator.Lock
import com.tezov.tuucho.core.domain.business.mock.coroutineTestScope
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol.Type
import dev.mokkery.answering.returns
import dev.mokkery.answering.returnsBy
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InteractionLockRepositoryTest {
    private val coroutineTestScope = coroutineTestScope()
    private lateinit var lockGenerator: InteractionLockGenerator
    private lateinit var sut: InteractionLockRepository
    private lateinit var counters: MutableMap<Type, Int>

    private fun stubLock(
        type: Type
    ) {
        every { lockGenerator.generate(type) } returnsBy {
            val count = counters.getValue(type).also {
                counters[type] = it + 1
            }
            Lock.Element(
                type = type,
                value = idKey(type, count)
            )
        }
    }

    private fun idKey(
        type: Type,
        count: Int
    ) = "${type}_$count"

    @BeforeTest
    fun setup() {
        lockGenerator = mock()
        sut = InteractionLockRepository(coroutineTestScope.createMock(), lockGenerator)
        counters = mutableMapOf<Type, Int>().withDefault { 1 }
        listOf(Type.ScreenInteraction, Type.Navigation).forEach {
            stubLock(it)
        }
    }

    @Test
    fun `lock type are different`() = coroutineTestScope.run {
        assertEquals(2, listOf(Type.ScreenInteraction, Type.Navigation).distinct().size)
        assertEquals(2, listOf(Type.ScreenInteraction, Type.Navigation).map { it.toString() }.distinct().size)
    }

    @Test
    fun `acquire empty list throws`() = coroutineTestScope.run {
        assertFailsWith<DomainException.Default> {
            sut.acquire(emptyList())
        }
    }

    @Test
    fun `tryAcquire with empty list returns null`() = coroutineTestScope.run {
        assertFailsWith<DomainException.Default> {
            sut.tryAcquire(emptyList())
        }
    }

    @Test
    fun `acquire single lock`() = coroutineTestScope.run {
        val lock = sut.acquire(Type.ScreenInteraction)

        assertEquals(Type.ScreenInteraction, lock.type)
        assertEquals(idKey(Type.ScreenInteraction, 1), lock.value)
        assertTrue(lock.canBeRelease)

        verify(VerifyMode.exactly(1)) {
            lockGenerator.generate(Type.ScreenInteraction)
        }
    }

    @Test
    fun `acquire and release single lock`() = coroutineTestScope.run {
        val lock = sut.acquire(Type.ScreenInteraction)
        sut.release(lock)

        val result = sut.tryAcquire(Type.ScreenInteraction)
        assertNotNull(result)

        verify(VerifyMode.exactly(2)) {
            lockGenerator.generate(Type.ScreenInteraction)
        }
    }

    @Test
    fun `acquire multiple lock`() = coroutineTestScope.run {
        val expectedTypes = listOf(Type.ScreenInteraction, Type.Navigation)

        val lock = sut.acquire(expectedTypes)

        assertTrue(lock is Lock.ElementArray)
        assertEquals(expectedTypes, lock.locks.map { it.type })
        lock.locks.forEach {
            when (it.type) {
                Type.ScreenInteraction -> assertEquals(it.value, idKey(Type.ScreenInteraction, 1))
                Type.Navigation -> assertEquals(it.value, idKey(Type.Navigation, 1))
            }
        }

        verify(VerifyMode.exactly(1)) {
            lockGenerator.generate(Type.ScreenInteraction)
        }
        verify(VerifyMode.exactly(1)) {
            lockGenerator.generate(Type.Navigation)
        }
    }

    @Test
    fun `tryAcquire single lock returns null when lock already held`() = coroutineTestScope.run {
        sut.acquire(Type.Navigation)
        verify(VerifyMode.exactly(1)) {
            lockGenerator.generate(Type.Navigation)
        }

        val failedAttempt = sut.tryAcquire(Type.Navigation)
        assertNull(failedAttempt)

        verify(VerifyMode.exactly(0)) {
            lockGenerator.generate(any())
        }
    }

    @Test
    fun `tryAcquire multiple lock returns null when at least one lock already held`() = coroutineTestScope.run {
        sut.acquire(Type.Navigation)
        verify(VerifyMode.exactly(1)) {
            lockGenerator.generate(Type.Navigation)
        }

        val failedAttempt = sut.tryAcquire(listOf(Type.ScreenInteraction, Type.Navigation))
        assertNull(failedAttempt)

        verify(VerifyMode.exactly(0)) {
            lockGenerator.generate(any())
        }
    }

    @Test
    fun `multi-lock acquire is atomic`() = coroutineTestScope.run {
        val combinedLock = sut.acquire(listOf(Type.ScreenInteraction, Type.Navigation))
        assertNotNull(combinedLock)
        verify(VerifyMode.exactly(1)) {
            lockGenerator.generate(Type.ScreenInteraction)
        }
        verify(VerifyMode.exactly(1)) {
            lockGenerator.generate(Type.Navigation)
        }

        val attemptScreen = sut.tryAcquire(Type.ScreenInteraction)
        assertNull(attemptScreen)

        val attemptNavigation = sut.tryAcquire(Type.Navigation)
        assertNull(attemptNavigation)

        verify(VerifyMode.exactly(0)) {
            lockGenerator.generate(any())
        }
    }

    @Test
    fun `second waiter suspends until first releases`() = coroutineTestScope.run {
        val firstLock = sut.acquire(Type.ScreenInteraction)

        val secondLockDeferred = async {
            sut.acquire(Type.ScreenInteraction)
        }

        delay(5)
        assertTrue(secondLockDeferred.isActive)

        sut.release(firstLock)
        val secondLock = secondLockDeferred.await()
        assertEquals(Type.ScreenInteraction, secondLock.type)
        assertEquals(idKey(Type.ScreenInteraction, 2), secondLock.value)

        verify(VerifyMode.exactly(2)) {
            lockGenerator.generate(Type.ScreenInteraction)
        }
    }

    @Test
    fun `wakes up waiters in correct order`() = coroutineTestScope.run {
        val expectedOrder = listOf(
            "first_waiter_completed",
            "second_waiter_completed"
        )
        val completionOrder = mutableListOf<String>()

        val initialLock = sut.acquire(Type.ScreenInteraction)

        val firstLockDeferred = async {
            sut.acquire(Type.ScreenInteraction).also {
                completionOrder += "first_waiter_completed"
            }
        }

        val secondLockDeferred = async {
            sut.acquire(Type.ScreenInteraction).also {
                completionOrder += "second_waiter_completed"
            }
        }

        delay(5)
        assertTrue(firstLockDeferred.isActive)
        assertTrue(secondLockDeferred.isActive)

        sut.release(initialLock)
        sut.release(firstLockDeferred.await())
        secondLockDeferred.await()

        assertTrue(completionOrder == expectedOrder)

        verify(VerifyMode.exactly(3)) {
            lockGenerator.generate(Type.ScreenInteraction)
        }
    }

    @Test
    fun `selective wake-up only releases eligible waiter`() = coroutineTestScope.run {
        val expectedTypes = listOf(Type.ScreenInteraction, Type.Navigation)

        val heldScreenLock = sut.acquire(Type.ScreenInteraction)

        val waiterA = async {
            sut.acquire(listOf(Type.ScreenInteraction, Type.Navigation))
        }

        val heldNavigationLockDeferred = async {
            sut.acquire(Type.Navigation)
        }

        delay(5)
        assertTrue(waiterA.isActive)
        assertFalse(heldNavigationLockDeferred.isActive)

        sut.release(heldScreenLock)
        delay(5)
        // NavigationLock is still unavailable, waiterA can't be done
        assertTrue(waiterA.isActive)

        sut.release(heldNavigationLockDeferred.await())
        delay(5)
        // ScreenLock and NavigationLock has been taken by waiterA
        assertFalse(waiterA.isActive)

        val lock = waiterA.await()
        assertTrue(lock is Lock.ElementArray)
        assertEquals(lock.locks.map { it.type }, expectedTypes)
        lock.locks.forEach {
            when (it.type) {
                Type.ScreenInteraction -> assertEquals(it.value, idKey(Type.ScreenInteraction, 2))
                Type.Navigation -> assertEquals(it.value, idKey(Type.Navigation, 2))
            }
        }

        verify(VerifyMode.exactly(2)) {
            lockGenerator.generate(Type.ScreenInteraction)
        }
        verify(VerifyMode.exactly(2)) {
            lockGenerator.generate(Type.Navigation)
        }
    }

    @Test
    fun `release ignores locks that cannot be released`() = coroutineTestScope.run {
        val unreleasable = Lock.Element(
            type = Type.ScreenInteraction,
            value = idKey(Type.ScreenInteraction, counters.getValue(Type.ScreenInteraction)),
            canBeRelease = false
        )
        every { lockGenerator.generate(Type.ScreenInteraction) } returns unreleasable

        val acquired = sut.acquire(Type.ScreenInteraction)
        assertTrue(acquired === unreleasable)

        val waiter = async {
            sut.acquire(Type.ScreenInteraction)
        }

        delay(5)
        assertTrue(waiter.isActive)

        sut.release(acquired)
        delay(5)

        assertTrue(waiter.isActive)
        waiter.cancel()

        verify(VerifyMode.exactly(1)) {
            lockGenerator.generate(Type.ScreenInteraction)
        }
    }

    @Test
    fun `release wakes only one waiter even when two are eligible`() = coroutineTestScope.run {
        val lock = sut.acquire(Type.ScreenInteraction)

        val waiter1 = async { sut.acquire(Type.ScreenInteraction) }
        val waiter2 = async { sut.acquire(Type.ScreenInteraction) }

        delay(5)
        assertTrue(waiter1.isActive)
        assertTrue(waiter2.isActive)

        sut.release(lock)

        val resumed = waiter1.await()
        assertNotNull(resumed)
        assertTrue(waiter2.isActive)

        waiter2.cancel()

        verify(VerifyMode.exactly(2)) {
            lockGenerator.generate(Type.ScreenInteraction)
        }
    }

    @Test
    fun `isValid returns true when lock is current`() = coroutineTestScope.run {
        val lock = sut.acquire(Type.ScreenInteraction)
        val result = sut.isValid(lock)
        assertTrue(result)
    }

    @Test
    fun `isValid returns false after release`() = coroutineTestScope.run {
        val lock = sut.acquire(Type.ScreenInteraction)
        sut.release(lock)
        val result = sut.isValid(lock)
        assertFalse(result)
    }

    @Test
    fun `isValid returns false for different instance with same type but different value`() = coroutineTestScope.run {
        val original = sut.acquire(Type.ScreenInteraction)
        val fake = Lock.Element(
            type = Type.ScreenInteraction,
            value = "fake"
        )
        val result = sut.isValid(fake)
        assertFalse(result)
        assertTrue(sut.isValid(original))
    }

    @Test
    fun `isValid returns false when another lock replaced it`() = coroutineTestScope.run {
        val first = sut.acquire(Type.ScreenInteraction)
        sut.release(first)
        val second = sut.acquire(Type.ScreenInteraction)
        val firstResult = sut.isValid(first)
        val secondResult = sut.isValid(second)
        assertFalse(firstResult)
        assertTrue(secondResult)
    }

    @Test
    fun `isValid returns false for lock with different type`() = coroutineTestScope.run {
        val validLock = sut.acquire(Type.Navigation)

        val fakeLock = Lock.Element(
            type = Type.ScreenInteraction,
            value = validLock.value
        )

        assertTrue(sut.isValid(validLock))
        assertFalse(sut.isValid(fakeLock))
    }
}
