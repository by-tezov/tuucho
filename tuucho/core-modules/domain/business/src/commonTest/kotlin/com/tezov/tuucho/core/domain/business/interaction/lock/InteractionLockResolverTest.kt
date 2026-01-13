package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLock
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class InteractionLockResolverTest {
    private lateinit var repo: InteractionLockProtocol.Stack
    private lateinit var sut: InteractionLockResolver

    private val requester = "test"

    private fun lock(
        type: InteractionLockType,
        owner: String = requester,
        id: String = "${type}_id"
    ) = InteractionLock(
        owner = owner,
        id = id,
        type = type
    )

    @BeforeTest
    fun setup() {
        repo = mock()
        sut = InteractionLockResolver(repo)
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(repo)
    }

    @Test
    fun `acquire with type only acquires all`() = runTest {
        val acquired = lock(InteractionLockType.Screen)
        everySuspend { repo.acquire(requester, listOf(InteractionLockType.Screen)) } returns listOf(acquired)

        val result = sut.acquire(
            requester,
            InteractionLockable.Type(listOf(InteractionLockType.Screen))
        )

        assertTrue(result is InteractionLockable.Lock)
        assertEquals(listOf(acquired), result.value)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            repo.acquire(requester, listOf(InteractionLockType.Screen))
        }
    }

    @Test
    fun `acquire skips already acquired types`() = runTest {
        val held = lock(InteractionLockType.Screen)
        val newLock = lock(InteractionLockType.Navigation)

        everySuspend { repo.isAllValid(listOf(held)) } returns true
        everySuspend { repo.acquire(requester, listOf(InteractionLockType.Navigation)) } returns listOf(newLock)

        val input = InteractionLockable.Lock(listOf(held)) +
            InteractionLockable.Type(
                listOf(InteractionLockType.Screen, InteractionLockType.Navigation)
            )

        val result = sut.acquire(requester, input)

        assertTrue(result is InteractionLockable.Lock)
        assertEquals(listOf(held, newLock), result.value)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            repo.isAllValid(listOf(held))
            repo.acquire(requester, listOf(InteractionLockType.Navigation))
        }
    }

    @Test
    fun `acquire throws when provided locks are invalid`() = runTest {
        val valid = lock(InteractionLockType.Screen)
        val invalid = lock(InteractionLockType.Navigation, owner = "other")

        val locks = listOf(valid, invalid)

        everySuspend { repo.isAllValid(locks) } returns false
        everySuspend { repo.isValid(valid) } returns true
        everySuspend { repo.isValid(invalid) } returns false

        val input = InteractionLockable.Lock(locks)

        assertFailsWith<DomainException.Default> {
            sut.acquire(requester, input)
        }

        verifySuspend(VerifyMode.exhaustiveOrder) {
            repo.isAllValid(locks)
            repo.isValid(valid)
            repo.isValid(invalid)
        }
    }

    @Test
    fun `tryAcquire returns Empty when nothing acquired`() = runTest {
        everySuspend { repo.tryAcquire(requester, listOf(InteractionLockType.Screen)) } returns null

        val result = sut.tryAcquire(
            requester,
            InteractionLockable.Type(listOf(InteractionLockType.Screen))
        )

        assertEquals(InteractionLockable.Empty, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            repo.tryAcquire(requester, listOf(InteractionLockType.Screen))
        }
    }

    @Test
    fun `tryAcquire merges already held and newly acquired`() = runTest {
        val held = lock(InteractionLockType.Screen)
        val newLock = lock(InteractionLockType.Navigation)

        val heldLocks = listOf(held)

        everySuspend { repo.isAllValid(heldLocks) } returns true
        everySuspend { repo.tryAcquire(requester, listOf(InteractionLockType.Navigation)) } returns listOf(newLock)

        val input = InteractionLockable.Lock(heldLocks) +
            InteractionLockable.Type(
                listOf(InteractionLockType.Screen, InteractionLockType.Navigation)
            )

        val result = sut.tryAcquire(requester, input)

        assertTrue(result is InteractionLockable.Lock)
        assertEquals(listOf(held, newLock), result.value)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            repo.isAllValid(heldLocks)
            repo.tryAcquire(requester, listOf(InteractionLockType.Navigation))
        }
    }

    @Test
    fun `tryAcquire throws when provided locks are invalid`() = runTest {
        val valid = lock(InteractionLockType.Screen)
        val invalid = lock(InteractionLockType.Navigation, owner = "other")

        val locks = listOf(valid, invalid)

        everySuspend { repo.isAllValid(locks) } returns false
        everySuspend { repo.isValid(valid) } returns true
        everySuspend { repo.isValid(invalid) } returns false

        val input = InteractionLockable.Lock(locks)

        assertFailsWith<DomainException.Default> {
            sut.tryAcquire(requester, input)
        }

        verifySuspend(VerifyMode.exhaustiveOrder) {
            repo.isAllValid(locks)
            repo.isValid(valid)
            repo.isValid(invalid)
        }
    }

    @Test
    fun `release on Empty does nothing`() = runTest {
        sut.release(requester, InteractionLockable.Empty)
    }

    @Test
    fun `release on Lock delegates to repository`() = runTest {
        val locks = listOf(lock(InteractionLockType.Screen))

        everySuspend { repo.release(requester, locks) } returns Unit

        sut.release(requester, InteractionLockable.Lock(locks))

        verifySuspend(VerifyMode.exhaustiveOrder) {
            repo.release(requester, locks)
        }
    }

    @Test
    fun `release on Composite delegates lock part only`() = runTest {
        val locks = listOf(lock(InteractionLockType.Screen))

        everySuspend { repo.release(requester, locks) } returns Unit

        val input = InteractionLockable.Composite(
            type = InteractionLockable.Type(listOf(InteractionLockType.Screen)),
            lock = InteractionLockable.Lock(locks)
        )

        sut.release(requester, input)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            repo.release(requester, locks)
        }
    }

    @Test
    fun `acquire with lock and no types returns same locks`() = runTest {
        val heldLocks = listOf(lock(InteractionLockType.Screen))

        everySuspend { repo.isAllValid(heldLocks) } returns true
        everySuspend { repo.acquire(requester, emptyList()) } returns emptyList()

        val input = InteractionLockable.Lock(heldLocks)
        val result = sut.acquire(requester, input)

        assertTrue(result is InteractionLockable.Lock)
        assertEquals(heldLocks, result.value)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            repo.isAllValid(heldLocks)
            repo.acquire(requester, emptyList())
        }
    }

    @Test
    fun `tryAcquire with lock and no types returns same locks`() = runTest {
        val heldLock = lock(InteractionLockType.Screen)
        val heldLocks = listOf(heldLock)

        everySuspend { repo.isAllValid(heldLocks) } returns true
        everySuspend { repo.tryAcquire(requester, emptyList()) } returns null

        val input = InteractionLockable.Lock(heldLocks)
        val result = sut.tryAcquire(requester, input)

        assertTrue(result is InteractionLockable.Lock)
        assertEquals(heldLocks, result.value)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            repo.isAllValid(heldLocks)
            repo.tryAcquire(requester, emptyList())
        }
    }
}
