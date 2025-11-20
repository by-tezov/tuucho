package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockGenerator.Lock
import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockMonitor.Event
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol.Type
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@OpenForTest
internal class InteractionLockRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val lockGenerator: InteractionLockGenerator,
    private val interactionLockMonitor: InteractionLockMonitor?
) : InteractionLockRepositoryProtocol {
    private data class Waiter(
        val requester: String,
        val lockTypes: List<Type>,
        val deferred: CompletableDeferred<Unit>
    )

    private val mutex = Mutex()
    private val usedLocks = mutableMapOf<Type, Lock.Element>()
    private val waiters = ArrayDeque<Waiter>()

    override suspend fun isValid(
        lock: Lock.Element
    ) = coroutineScopes.default.await {
        mutex.withLock {
            usedLocks[lock.type] == lock
        }
    }

    override suspend fun acquire(
        requester: String,
        types: List<Type>
    ): Lock = coroutineScopes.default.await {
        tryAcquireInternal(requester, types)?.let { lock ->
            interactionLockMonitor?.process(
                InteractionLockMonitor.Context(
                    event = Event.Acquire,
                    requester = requester,
                    lockTypes = when (lock) {
                        is Lock.Element -> listOf(lock.type)
                        is Lock.ElementArray -> lock.locks.map { it.type }
                    }
                ))
            return@await lock
        }
        coroutineScopes.io.await {
            interactionLockMonitor?.process(
                InteractionLockMonitor.Context(
                    event = Event.WaitToAcquire,
                    requester = requester,
                    lockTypes = types
                )
            )
            val waiter = CompletableDeferred<Unit>()
            mutex.withLock {
                waiters.add(
                    Waiter(
                        requester = requester,
                        lockTypes = types,
                        deferred = waiter
                    )
                )
            }
            waiter.await()
        }
        acquire(requester, types)
    }

    override suspend fun tryAcquire(
        requester: String,
        types: List<Type>
    ): Lock? = coroutineScopes.default.await {
        tryAcquireInternal(requester, types).also { lock ->
            interactionLockMonitor?.process(
                InteractionLockMonitor.Context(
                    event = Event.TryAcquire,
                    requester = requester,
                    lockTypes = when (lock) {
                        is Lock.Element -> listOf(lock.type)
                        is Lock.ElementArray -> lock.locks.map { it.type }
                        null -> emptyList()
                    }
                ))
        }
    }

    private suspend fun tryAcquireInternal(
        requester: String,
        types: List<Type>
    ): Lock? {
        if (types.isEmpty()) {
            throw DomainException.Default("Lock types cannot be empty")
        }
        val ordered = types.sortedBy { it.ordinal }
        var acquired: List<Lock.Element>? = null
        mutex.withLock {
            if (!ordered.all { it !in usedLocks }) return@withLock
            acquired = types.map { type ->
                lockGenerator.generate(
                    InteractionLockGenerator.Input(
                        owner = requester,
                        type = type
                    )
                ).also { usedLocks[type] = it }
            }
        }
        return acquired?.let {
            when (it.size) {
                1 -> it.first()
                else -> Lock.ElementArray(requester, it)
            }
        }
    }

    override suspend fun acquire(
        requester: String,
        type: Type
    ) = acquire(requester, listOf(type)) as Lock.Element

    override suspend fun tryAcquire(
        requester: String,
        type: Type
    ) = tryAcquire(requester, listOf(type)) as Lock.Element?

    override suspend fun release(
        requester: String,
        lock: Lock
    ) {
        when (lock) {
            is Lock.Element -> releaseElement(requester, lock)
            is Lock.ElementArray -> lock.locks.forEach { releaseElement(requester, it) }
        }
        interactionLockMonitor?.process(
            InteractionLockMonitor.Context(
                event = Event.Release,
                requester = if (requester == lock.owner) {
                    requester
                } else {
                    "requester $requester but owned by ${lock.owner}"
                },
                lockTypes = when (lock) {
                    is Lock.Element -> listOf(lock.type)
                    is Lock.ElementArray -> lock.locks.map { it.type }
                }
            ))
    }

    private suspend fun releaseElement(
        requester: String,
        lock: Lock.Element
    ) {
        if (!lock.canBeRelease) {
            interactionLockMonitor?.process(
                InteractionLockMonitor.Context(
                    event = Event.CanNotBeRelease,
                    requester = if (requester == lock.owner) {
                        requester
                    } else {
                        "requester $requester but owned by ${lock.owner}"
                    },
                    lockTypes = listOf(lock.type)
                )
            )
            return
        }
        coroutineScopes.default.await {
            mutex.withLock {
                usedLocks.remove(lock.type)
            }
            coroutineScopes.io.async {
                var toResume: Waiter? = null
                mutex.withLock {
                    toResume = waiters
                        .firstOrNull { waiter ->
                            waiter.lockTypes.all { it !in usedLocks }
                        }?.also {
                            waiters.remove(it)
                            interactionLockMonitor?.process(
                                InteractionLockMonitor.Context(
                                    event = Event.TryAcquireAgain,
                                    requester = it.requester,
                                    lockTypes = it.lockTypes
                                )
                            )
                        }
                }
                toResume?.deferred?.complete(Unit)
            }
        }
    }
}
