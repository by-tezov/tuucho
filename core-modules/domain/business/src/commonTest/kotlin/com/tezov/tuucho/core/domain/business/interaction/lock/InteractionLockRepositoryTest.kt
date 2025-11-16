package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockGenerator.Lock
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
import kotlinx.coroutines.test.runTest
import java.lang.ref.WeakReference
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InteractionLockRepositoryTest {
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
                lockRepositoryRef = WeakReference(sut),
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
        sut = InteractionLockRepository(lockGenerator)
        counters = mutableMapOf<Type, Int>().withDefault { 1 }
        listOf(Type.ScreenInteraction, Type.Navigation).forEach {
            stubLock(it)
        }
    }

    @Test
    fun `lock type are different`() = runTest {
        assertEquals(2, listOf(Type.ScreenInteraction, Type.Navigation).distinct().size)
        assertEquals(2, listOf(Type.ScreenInteraction, Type.Navigation).map { it.toString() }.distinct().size)
    }

    @Test
    fun `acquire empty list throws`() = runTest {
        assertFailsWith<DomainException.Default> {
            sut.acquire(emptyList())
        }
    }

    @Test
    fun `tryAcquire with empty list returns null`() = runTest {
        assertFailsWith<DomainException.Default> {
            sut.tryAcquire(emptyList())
        }
    }

    @Test
    fun `acquire single lock`() = runTest {
        val lock = sut.acquire(Type.ScreenInteraction)

        assertEquals(Type.ScreenInteraction, lock.type)
        assertEquals(idKey(Type.ScreenInteraction, 1), lock.value)
        assertTrue(lock.canBeRelease)

        verify(VerifyMode.exactly(1)) {
            lockGenerator.generate(Type.ScreenInteraction)
        }
    }

    @Test
    fun `acquire and release single lock`() = runTest {
        val lock = sut.acquire(Type.ScreenInteraction)
        lock.release()

        val result = sut.tryAcquire(Type.ScreenInteraction)
        assertNotNull(result)

        verify(VerifyMode.exactly(2)) {
            lockGenerator.generate(Type.ScreenInteraction)
        }
    }

    @Test
    fun `acquire multiple lock`() = runTest {
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
    fun `tryAcquire single lock returns null when lock already held`() = runTest {
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
    fun `tryAcquire multiple lock returns null when at least one lock already held`() = runTest {
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
    fun `multi-lock acquire is atomic`() = runTest {
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
    fun `second waiter suspends until first releases`() = runTest {
        val firstLock = sut.acquire(Type.ScreenInteraction)

        val secondLockDeferred = async {
            sut.acquire(Type.ScreenInteraction)
        }

        delay(5)
        assertTrue(secondLockDeferred.isActive)

        firstLock.release()
        val secondLock = secondLockDeferred.await()
        assertEquals(Type.ScreenInteraction, secondLock.type)
        assertEquals(idKey(Type.ScreenInteraction, 2), secondLock.value)

        verify(VerifyMode.exactly(2)) {
            lockGenerator.generate(Type.ScreenInteraction)
        }
    }

    @Test
    fun `wakes up waiters in correct order`() = runTest {
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

        initialLock.release()
        firstLockDeferred.await().release()
        secondLockDeferred.await()

        assertTrue(completionOrder == expectedOrder)

        verify(VerifyMode.exactly(3)) {
            lockGenerator.generate(Type.ScreenInteraction)
        }
    }

    @Test
    fun `selective wake-up only releases eligible waiter`() = runTest {
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

        heldScreenLock.release()
        delay(5)
        // NavigationLock is still unavailable, waiterA can't be done
        assertTrue(waiterA.isActive)

        heldNavigationLockDeferred.await().release()
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
    fun `release ignores locks that cannot be released`() = runTest {
        val unreleasable = Lock.Element(
            lockRepositoryRef = WeakReference(sut),
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

        acquired.release()
        delay(5)

        assertTrue(waiter.isActive)
        waiter.cancel()

        verify(VerifyMode.exactly(1)) {
            lockGenerator.generate(Type.ScreenInteraction)
        }
    }

    @Test
    fun `release wakes only one waiter even when two are eligible`() = runTest {
        val lock = sut.acquire(Type.ScreenInteraction)

        val waiter1 = async { sut.acquire(Type.ScreenInteraction) }
        val waiter2 = async { sut.acquire(Type.ScreenInteraction) }

        delay(5)
        assertTrue(waiter1.isActive)
        assertTrue(waiter2.isActive)

        lock.release()

        val resumed = waiter1.await()
        assertNotNull(resumed)
        assertTrue(waiter2.isActive)

        waiter2.cancel()

        verify(VerifyMode.exactly(2)) {
            lockGenerator.generate(Type.ScreenInteraction)
        }
    }

    @Test
    fun `isValid returns true when lock is current`() = runTest {
        val lock = sut.acquire(Type.ScreenInteraction)
        val result = sut.isValid(lock)
        assertTrue(result)
    }

    @Test
    fun `isValid returns false after release`() = runTest {
        val lock = sut.acquire(Type.ScreenInteraction)
        lock.release()
        val result = sut.isValid(lock)
        assertFalse(result)
    }

    @Test
    fun `isValid returns false for different instance with same type but different value`() = runTest {
        val original = sut.acquire(Type.ScreenInteraction)
        val fake = Lock.Element(
            lockRepositoryRef = WeakReference(sut),
            type = Type.ScreenInteraction,
            value = "fake"
        )
        val result = sut.isValid(fake)
        assertFalse(result)
        assertTrue(sut.isValid(original))
    }

    @Test
    fun `isValid returns false when another lock replaced it`() = runTest {
        val first = sut.acquire(Type.ScreenInteraction)
        first.release()
        val second = sut.acquire(Type.ScreenInteraction)
        val firstResult = sut.isValid(first)
        val secondResult = sut.isValid(second)
        assertFalse(firstResult)
        assertTrue(secondResult)
    }

    @Test
    fun `isValid returns false for lock with different type`() = runTest {
        val validLock = sut.acquire(Type.Navigation)

        val fakeLock = Lock.Element(
            lockRepositoryRef = WeakReference(sut),
            type = Type.ScreenInteraction,
            value = validLock.value
        )

        assertTrue(sut.isValid(validLock))
        assertFalse(sut.isValid(fakeLock))
    }
}
