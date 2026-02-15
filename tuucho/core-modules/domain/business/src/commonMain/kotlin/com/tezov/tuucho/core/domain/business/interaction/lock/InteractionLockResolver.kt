package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLock
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable

internal class InteractionLockResolver(
    private val repository: InteractionLockProtocol.Stack
) : InteractionLockProtocol.Resolver {
    override suspend fun acquire(
        requester: String,
        lockable: InteractionLockable
    ): InteractionLockable {
        val locksAlreadyAcquired = lockable.getLocks().also {
            it.verify(requester)
        }
        val lockAlreadyAcquiredTypes = locksAlreadyAcquired.map { it.type }
        val types = lockable
            .getTypes()
            .filter { it !in lockAlreadyAcquiredTypes }
            .acquire(requester)
        return InteractionLockable.Locks(locksAlreadyAcquired + types)
    }

    override suspend fun tryAcquire(
        requester: String,
        lockable: InteractionLockable
    ): InteractionLockable {
        val locksAlreadyAcquired = lockable.getLocks().also {
            it.verify(requester)
        }
        val lockAlreadyAcquiredTypes = locksAlreadyAcquired.map { it.type }
        val types = lockable
            .getTypes()
            .filter { it !in lockAlreadyAcquiredTypes }
            .tryAcquire(requester)
            ?: emptyList()
        return (locksAlreadyAcquired + types)
            .takeIf { it.isNotEmpty() }
            ?.let(InteractionLockable::Locks)
            ?: InteractionLockable.Empty
    }

    private suspend fun List<InteractionLockType>.acquire(
        requester: String,
    ) = repository.acquire(requester, this)

    private suspend fun List<InteractionLockType>.tryAcquire(
        requester: String,
    ) = repository.tryAcquire(requester, this)

    private suspend fun List<InteractionLock>.verify(
        requester: String
    ) {
        val isAllValid = all { repository.isAllValid(this) }
        if (!isAllValid) {
            val message = mapNotNull {
                if (repository.isValid(it)) {
                    null
                } else {
                    "${it.type}:${it.owner}"
                }
            }
            throw DomainException.Default("requester $requester provide invalid locks => $message")
        }
    }

    private suspend fun List<InteractionLock>.release(
        requester: String
    ) {
        repository.release(requester, this)
    }

    override suspend fun release(
        requester: String,
        lockable: InteractionLockable
    ) {
        when (lockable) {
            is InteractionLockable.Locks -> {
                lockable.values.release(requester)
            }

            is InteractionLockable.Composite -> {
                release(requester, lockable.locks)
            }

            else -> {}
        }
    }
}
