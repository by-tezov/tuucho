package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockGenerator.Lock
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol.Type
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@OpenForTest
internal class InteractionLockRepository(
    private val lockGenerator: InteractionLockGenerator,
) : InteractionLockRepositoryProtocol {
    private data class Waiter(
        val lockTypes: List<Type>,
        val deferred: CompletableDeferred<Unit>
    )

    private val mutex = Mutex()
    private val usedLocks = mutableMapOf<Type, Lock.Element>()
    private val waiters = ArrayDeque<Waiter>()

    override suspend fun isValid(
        lock: Lock.Element
    ): Boolean {
        mutex.withLock {
            return usedLocks[lock.type] == lock
        }
    }

    override suspend fun acquire(
        types: List<Type>
    ): Lock {
        tryAcquire(types)?.let { return it }
        val waiter = CompletableDeferred<Unit>()
        mutex.withLock {
            waiters.add(
                Waiter(
                    lockTypes = types,
                    deferred = waiter
                )
            )
        }
        waiter.await()
        return acquire(types)
    }

    override suspend fun tryAcquire(
        types: List<Type>
    ): Lock? {
        if (types.isEmpty()) {
            throw DomainException.Default("Lock types cannot be empty")
        }
        val ordered = types.sortedBy { it.ordinal }
        var acquired: List<Lock.Element>? = null
        mutex.withLock {
            if (!ordered.all { it !in usedLocks }) return@withLock null
            acquired = types.map { type ->
                lockGenerator.generate(type).also { usedLocks[type] = it }
            }
        }
        return acquired?.let {
            when (it.size) {
                1 -> it.first()
                else -> Lock.ElementArray(it)
            }
        }
    }

    override suspend fun acquire(
        type: Type
    ) = acquire(listOf(type)) as Lock.Element

    override suspend fun tryAcquire(
        type: Type
    ) = tryAcquire(listOf(type)) as Lock.Element?

    override suspend fun release(
        lock: Lock
    ) {
        when (lock) {
            is Lock.Element -> releaseElement(lock)
            is Lock.ElementArray -> lock.locks.forEach { releaseElement(it) }
        }
    }

    private suspend fun releaseElement(
        lock: Lock.Element
    ) {
        if (!lock.canBeRelease) {
            return
        }
        val toResume: Waiter?
        mutex.withLock {
            usedLocks.remove(lock.type)
            toResume = waiters
                .firstOrNull { waiter ->
                    waiter.lockTypes.all { it !in usedLocks }
                }?.also {
                    waiters.remove(it)
                }
        }
        toResume?.deferred?.complete(Unit)
    }
}
