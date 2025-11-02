package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class InteractionLockRepository(
    private val idGenerator: IdGeneratorProtocol,
) : InteractionLockRepositoryProtocol {
    private val locks = mutableMapOf<InteractionLockRepositoryProtocol.Type, String>()
    private val mutex = Mutex()

    override suspend fun tryLock(
        type: InteractionLockRepositoryProtocol.Type
    ): String? = mutex.withLock {
        if (locks.containsKey(type)) {
            null
        } else {
            idGenerator.generate().also {
                locks[type] = it
            }
        }
    }

    override suspend fun unLock(
        type: InteractionLockRepositoryProtocol.Type,
        handle: String
    ) {
        mutex.withLock {
            val current = locks[type]
            if (current == null || current != handle) {
                throw DomainException.Default("Invalid unlock handle for $type")
            }
            locks.remove(type)
        }
    }
}
