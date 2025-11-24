package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.mock.coroutineTestScope
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLock
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import dev.mokkery.answering.returns
import dev.mokkery.answering.returnsBy
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

// TODO redo cleanly
// include monitor and test if id is different from hold, then no release is done
class InteractionLockStackTest {
    private val coroutineTestScope = coroutineTestScope()
    private lateinit var lockGenerator: InteractionLockGenerator
    private lateinit var sut: InteractionLockRepositoryWrap
    private lateinit var counters: MutableMap<InteractionLockType, Int>

    companion object {
        private val requester = "test"
    }

    private class InteractionLockRepositoryWrap(
        coroutineScopes: CoroutineScopesProtocol,
        lockGenerator: InteractionLockGenerator,
    ) : InteractionLockStack(
            coroutineScopes = coroutineScopes,
            lockGenerator = lockGenerator,
            interactionLockMonitor = null
        ) {
        suspend fun acquire(
            type: InteractionLockType
        ) = acquire(
            requester = requester,
            type = type
        )

        suspend fun tryAcquire(
            type: InteractionLockType
        ) = tryAcquire(
            requester = requester,
            type = type
        )

        suspend fun acquire(
            types: List<InteractionLockType>
        ) = acquire(
            requester = requester,
            types = types
        )

        suspend fun tryAcquire(
            types: List<InteractionLockType>
        ) = tryAcquire(
            requester = requester,
            types = types
        )

        suspend fun release(
            lock: InteractionLock
        ) = release(
            requester = requester,
            lock = lock
        )
    }

    private fun generateInput(
        type: InteractionLockType
    ) = InteractionLockGenerator.Input(
        owner = "test",
        type = type
    )

    private fun stubLock(
        type: InteractionLockType
    ) {
        every { lockGenerator.generate(generateInput(type)) } returnsBy {
            val count = counters.getValue(type).also {
                counters[type] = it + 1
            }
            InteractionLock(
                owner = "test",
                id = idKey(type, count),
                type = type,
            )
        }
    }

    private fun idKey(
        type: InteractionLockType,
        count: Int
    ) = "${type}_$count"

    @BeforeTest
    fun setup() {
        lockGenerator = mock()
        sut = InteractionLockRepositoryWrap(
            coroutineScopes = coroutineTestScope.createMock(),
            lockGenerator = lockGenerator
        )
        counters = mutableMapOf<InteractionLockType, Int>().withDefault { 1 }
        listOf(InteractionLockType.Screen, InteractionLockType.Navigation).forEach {
            stubLock(it)
        }
    }

    @Test
    fun `lock type are different`() = coroutineTestScope.run {
        assertEquals(2, listOf(InteractionLockType.Screen, InteractionLockType.Navigation).distinct().size)
        assertEquals(2, listOf(InteractionLockType.Screen, InteractionLockType.Navigation).map { it.toString() }.distinct().size)
    }

    @Test
    fun `tryAcquire with empty list returns empty list`() = coroutineTestScope.run {
        val result = sut.tryAcquire(emptyList())
        assertEquals(emptyList(), result)
    }

    @Test
    fun `acquire single lock`() = coroutineTestScope.run {
        val lock = sut.acquire(InteractionLockType.Screen)

        assertEquals(InteractionLockType.Screen, lock.type)
        assertEquals(idKey(InteractionLockType.Screen, 1), lock.id)
        assertTrue(lock.canBeReleased)

//        verify(VerifyMode.exactly(1)) {
//            lockGenerator.generate(InteractionLockType.Screen)
//        }
    }

    @Test
    fun `acquire and release single lock`() = coroutineTestScope.run {
        val lock = sut.acquire(InteractionLockType.Screen)
        sut.release(lock)

        val result = sut.tryAcquire(InteractionLockType.Screen)
        assertNotNull(result)

//        verify(VerifyMode.exactly(2)) {
//            lockGenerator.generate(InteractionLockType.Screen)
//        }
    }

    @Test
    fun `acquire multiple lock`() = coroutineTestScope.run {
        val expectedTypes = listOf(InteractionLockType.Screen, InteractionLockType.Navigation)

        val locks = sut.acquire(expectedTypes)

        assertEquals(expectedTypes, locks.map { it.type })
        locks.forEach {
            when (it.type) {
                InteractionLockType.Screen -> assertEquals(it.id, idKey(InteractionLockType.Screen, 1))
                InteractionLockType.Navigation -> assertEquals(it.id, idKey(InteractionLockType.Navigation, 1))
            }
        }
//        verify(VerifyMode.exactly(1)) {
//            lockGenerator.generate(InteractionLockType.Screen)
//        }
//        verify(VerifyMode.exactly(1)) {
//            lockGenerator.generate(InteractionLockType.Navigation)
//        }
    }

    @Test
    fun `tryAcquire single lock returns null when lock already held`() = coroutineTestScope.run {
        sut.acquire(InteractionLockType.Navigation)
//        verify(VerifyMode.exactly(1)) {
//            lockGenerator.generate(InteractionLockType.Navigation)
//        }

        val failedAttempt = sut.tryAcquire(InteractionLockType.Navigation)
        assertNull(failedAttempt)

//        verify(VerifyMode.exactly(0)) {
//            lockGenerator.generate(any())
//        }
    }

    @Test
    fun `tryAcquire multiple lock returns null when at least one lock already held`() = coroutineTestScope.run {
        sut.acquire(InteractionLockType.Navigation)
//        verify(VerifyMode.exactly(1)) {
//            lockGenerator.generate(InteractionLockType.Navigation)
//        }

        val failedAttempt = sut.tryAcquire(listOf(InteractionLockType.Screen, InteractionLockType.Navigation))
        assertNull(failedAttempt)

//        verify(VerifyMode.exactly(0)) {
//            lockGenerator.generate(any())
//        }
    }

    @Test
    fun `multi-lock acquire is atomic`() = coroutineTestScope.run {
        val combinedLock = sut.acquire(listOf(InteractionLockType.Screen, InteractionLockType.Navigation))
        assertNotNull(combinedLock)
//        verify(VerifyMode.exactly(1)) {
//            lockGenerator.generate(InteractionLockType.Screen)
//        }
//        verify(VerifyMode.exactly(1)) {
//            lockGenerator.generate(InteractionLockType.Navigation)
//        }

        val attemptScreen = sut.tryAcquire(InteractionLockType.Screen)
        assertNull(attemptScreen)

        val attemptNavigation = sut.tryAcquire(InteractionLockType.Navigation)
        assertNull(attemptNavigation)

//        verify(VerifyMode.exactly(0)) {
//            lockGenerator.generate(any())
//        }
    }

    @Test
    fun `second waiter suspends until first releases`() = coroutineTestScope.run {
        // TODO
        val firstLock = sut.acquire(InteractionLockType.Screen)
        val secondLockDeferred = async {
            sut.acquire(InteractionLockType.Screen)
        }

        delay(5)
        assertTrue(secondLockDeferred.isActive)

        sut.release(firstLock)
        val secondLock = secondLockDeferred.await()

        assertEquals(InteractionLockType.Screen, secondLock.type)
        assertEquals(idKey(InteractionLockType.Screen, 2), secondLock.id)

//        verify(VerifyMode.exactly(2)) {
//            lockGenerator.generate(InteractionLockType.Screen)
//        }
    }

    @Test
    fun `wakes up waiters in correct order`() = coroutineTestScope.run {
        val expectedOrder = listOf(
            "first_waiter_completed",
            "second_waiter_completed"
        )
        val completionOrder = mutableListOf<String>()

        val initialLock = sut.acquire(InteractionLockType.Screen)

        val firstLockDeferred = async {
            sut.acquire(InteractionLockType.Screen).also {
                completionOrder += "first_waiter_completed"
            }
        }

        val secondLockDeferred = async {
            sut.acquire(InteractionLockType.Screen).also {
                completionOrder += "second_waiter_completed"
            }
        }

        delay(5)
        assertTrue(firstLockDeferred.isActive)
        assertTrue(secondLockDeferred.isActive)

        sut.release(initialLock)
        sut.release(firstLockDeferred.await())
        secondLockDeferred.await()

        assertEquals(expectedOrder, completionOrder)

//        verify(VerifyMode.exactly(3)) {
//            lockGenerator.generate(InteractionLockType.Screen)
//        }
    }

    @Test
    fun `selective wake-up only releases eligible waiter`() = coroutineTestScope.run {
        val expectedTypes = listOf(InteractionLockType.Screen, InteractionLockType.Navigation)

        val heldScreenLock = sut.acquire(InteractionLockType.Screen)

        val waiterA = async {
            sut.acquire(listOf(InteractionLockType.Screen, InteractionLockType.Navigation))
        }

        val heldNavigationLockDeferred = async {
            sut.acquire(InteractionLockType.Navigation)
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

        val locks = waiterA.await()
        assertEquals(locks.map { it.type }, expectedTypes)
        locks.forEach {
            when (it.type) {
                InteractionLockType.Screen -> assertEquals(it.id, idKey(InteractionLockType.Screen, 2))
                InteractionLockType.Navigation -> assertEquals(it.id, idKey(InteractionLockType.Navigation, 2))
            }
        }

//        verify(VerifyMode.exactly(2)) {
//            lockGenerator.generate(InteractionLockType.Screen)
//        }
//        verify(VerifyMode.exactly(2)) {
//            lockGenerator.generate(InteractionLockType.Navigation)
//        }
    }

    @Test
    fun `release ignores locks that cannot be released`() = coroutineTestScope.run {
        val unreleasable = InteractionLock(
            owner = "test",
            id = idKey(InteractionLockType.Screen, counters.getValue(InteractionLockType.Screen)),
            type = InteractionLockType.Screen,
            canBeReleased = false
        )
        every { lockGenerator.generate(generateInput(InteractionLockType.Screen)) } returns unreleasable

        val acquired = sut.acquire(InteractionLockType.Screen)
        assertSame(unreleasable, acquired)

        val waiter = async {
            sut.acquire(InteractionLockType.Screen)
        }

        delay(5)
        assertTrue(waiter.isActive)

        sut.release(acquired)
        delay(5)

        assertTrue(waiter.isActive)
        waiter.cancel()

//        verify(VerifyMode.exactly(1)) {
//            lockGenerator.generate(InteractionLockType.Screen)
//        }
    }

    @Test
    fun `release wakes only one waiter even when two are eligible`() = coroutineTestScope.run {
        val lock = sut.acquire(InteractionLockType.Screen)

        val waiter1 = async { sut.acquire(InteractionLockType.Screen) }
        val waiter2 = async { sut.acquire(InteractionLockType.Screen) }

        delay(5)
        assertTrue(waiter1.isActive)
        assertTrue(waiter2.isActive)

        sut.release(lock)

        val resumed = waiter1.await()
        assertNotNull(resumed)
        assertTrue(waiter2.isActive)

        waiter2.cancel()

//        verify(VerifyMode.exactly(2)) {
//            lockGenerator.generate(InteractionLockType.Screen)
//        }
    }

    @Test
    fun `isValid returns true when lock is current`() = coroutineTestScope.run {
        val lock = sut.acquire(InteractionLockType.Screen)
        val result = sut.isValid(lock)
        assertTrue(result)
    }

    @Test
    fun `isValid returns false after release`() = coroutineTestScope.run {
        val lock = sut.acquire(InteractionLockType.Screen)
        sut.release(lock)
        val result = sut.isValid(lock)
        assertFalse(result)
    }

    @Test
    fun `isValid returns false for different instance with same type but different value`() = coroutineTestScope.run {
        val original = sut.acquire(InteractionLockType.Screen)
        val fake = InteractionLock(
            owner = "test",
            id = "fake",
            type = InteractionLockType.Screen,
        )
        val result = sut.isValid(fake)
        assertFalse(result)
        assertTrue(sut.isValid(original))
    }

    @Test
    fun `isValid returns false when another lock replaced it`() = coroutineTestScope.run {
        val first = sut.acquire(InteractionLockType.Screen)
        sut.release(first)
        val second = sut.acquire(InteractionLockType.Screen)
        val firstResult = sut.isValid(first)
        val secondResult = sut.isValid(second)
        assertFalse(firstResult)
        assertTrue(secondResult)
    }

    @Test
    fun `isValid returns false for lock with different type`() = coroutineTestScope.run {
        val validLock = sut.acquire(InteractionLockType.Navigation)

        val fakeLock = InteractionLock(
            owner = "test",
            id = validLock.id,
            type = InteractionLockType.Screen,
        )

        assertTrue(sut.isValid(validLock))
        assertFalse(sut.isValid(fakeLock))
    }
}
