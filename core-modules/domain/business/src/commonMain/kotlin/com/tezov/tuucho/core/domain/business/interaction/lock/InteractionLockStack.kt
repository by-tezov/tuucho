package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockMonitor.Event
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLock
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.async.DeferredExtension.throwOnFailure
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@OpenForTest
internal class InteractionLockStack(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val lockGenerator: InteractionLockGenerator,
    private val interactionLockMonitor: InteractionLockMonitor?
) : InteractionLockProtocol.Stack {
    private data class Waiter(
        val requester: String,
        val lockTypes: List<InteractionLockType>,
        val deferred: CompletableDeferred<Unit>
    )

    private val mutex = Mutex()
    private val usedLocks = mutableMapOf<InteractionLockType, InteractionLock>()
    private val waiters = ArrayDeque<Waiter>()

    override suspend fun isValid(
        lock: InteractionLock
    ) = coroutineScopes.default.await {
        mutex.withLock {
            usedLocks[lock.type]?.id == lock.id
        }
    }

    override suspend fun isAllValid(
        locks: List<InteractionLock>
    ) = coroutineScopes.default.await {
        mutex.withLock {
            locks.all { usedLocks[it.type]?.id == it.id }
        }
    }

    override suspend fun acquire(
        requester: String,
        type: InteractionLockType
    ) = acquire(requester, listOf(type)).first()

    override suspend fun acquire(
        requester: String,
        types: List<InteractionLockType>
    ): List<InteractionLock> = coroutineScopes.default.await {
        tryAcquireInternal(requester, types)?.let { locks ->
            interactionLockMonitor?.process(InteractionLockMonitor.Context(
                event = Event.Acquired,
                requester = listOf(requester),
                lockTypes = locks.map { it.type }
            ))
            return@await locks
        }
        coroutineScopes.io.await {
            interactionLockMonitor?.process(
                InteractionLockMonitor.Context(
                    event = Event.WaitToAcquire,
                    requester = listOf(requester),
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
        type: InteractionLockType
    ) = tryAcquire(requester, listOf(type))?.firstOrNull()

    override suspend fun tryAcquire(
        requester: String,
        types: List<InteractionLockType>
    ): List<InteractionLock>? = coroutineScopes.default.await {
        tryAcquireInternal(requester, types)?.also { locks ->
            interactionLockMonitor?.process(InteractionLockMonitor.Context(
                event = Event.AcquireFromTry,
                requester = listOf(requester),
                lockTypes = locks.map { it.type }
            ))
        }
    }

    private suspend fun tryAcquireInternal(
        requester: String,
        types: List<InteractionLockType>
    ): List<InteractionLock>? {
        if (types.isEmpty()) {
            return emptyList()
        }
        val ordered = types.sortedBy { it.name }
        var acquired: List<InteractionLock>? = null
        mutex.withLock {
            if (!ordered.all { it !in usedLocks }) return@withLock
            acquired = types.map { type ->
                lockGenerator
                    .generate(
                        InteractionLockGenerator.Input(
                            owner = requester,
                            type = type
                        )
                    ).also { usedLocks[type] = it }
            }
        }
        return acquired?.takeIf { it.isNotEmpty() }
    }

    override suspend fun release(
        requester: String,
        locks: List<InteractionLock>
    ) {
        val hasBeenReleased = locks.mapNotNull {
            val hasBeenReleased = releaseInternal(
                requester = requester,
                lock = it
            )
            if (hasBeenReleased) {
                it.owner to it.type
            } else {
                null
            }
        }
        if (hasBeenReleased.isNotEmpty()) {
            interactionLockMonitor?.process(
                InteractionLockMonitor.Context(
                    event = Event.Released,
                    requester = hasBeenReleased.map { lock ->
                        if (requester == lock.first) {
                            requester
                        } else {
                            "requester $requester but owned by ${lock.first}"
                        }
                    },
                    lockTypes = hasBeenReleased.map { it.second }
                )
            )
            wakeup()
        }
    }

    override suspend fun release(
        requester: String,
        lock: InteractionLock
    ) {
        val hasBeenReleased = releaseInternal(
            requester = requester,
            lock = lock
        )
        if (hasBeenReleased) {
            interactionLockMonitor?.process(
                InteractionLockMonitor.Context(
                    event = Event.Released,
                    requester = listOf(
                        if (requester == lock.owner) {
                            requester
                        } else {
                            "requester $requester but owned by ${lock.owner}"
                        }
                    ),
                    lockTypes = listOf(lock.type)
                )
            )
            wakeup()
        }
    }

    private suspend fun releaseInternal(
        requester: String,
        lock: InteractionLock
    ): Boolean {
        if (!lock.canBeReleased) {
            interactionLockMonitor?.process(
                InteractionLockMonitor.Context(
                    event = Event.CanNotBeReleased,
                    requester = listOf(
                        if (requester == lock.owner) {
                            requester
                        } else {
                            "requester $requester but owned by ${lock.owner}"
                        }
                    ),
                    lockTypes = listOf(lock.type)
                )
            )
            return false
        }
        var canBeReleased = false
        coroutineScopes.default.await {
            mutex.withLock {
                canBeReleased = usedLocks[lock.type]?.id == lock.id
                if (canBeReleased) {
                    usedLocks.remove(lock.type)
                }
            }
        }
        return canBeReleased
    }

    private fun wakeup() {
        coroutineScopes.io.async {
            val toResumes = mutableListOf<Waiter>()
            mutex.withLock {
                val usedLocksKeys = usedLocks.keys.toMutableList()
                val waiters = waiters.listIterator()
                while (waiters.hasNext()) {
                    val next = waiters.next()
                    if (next.lockTypes.all { it !in usedLocksKeys }) {
                        waiters.remove()
                        toResumes.add(next)
                        interactionLockMonitor?.process(
                            InteractionLockMonitor.Context(
                                event = Event.TryAcquireAgain,
                                requester = listOf(next.requester),
                                lockTypes = next.lockTypes
                            )
                        )
                        usedLocksKeys.removeAll(next.lockTypes)
                        if (usedLocksKeys.isEmpty()) {
                            break
                        }
                    }
                }
            }
            toResumes.forEach { it.deferred.complete(Unit) }
        }.throwOnFailure()
    }
}
