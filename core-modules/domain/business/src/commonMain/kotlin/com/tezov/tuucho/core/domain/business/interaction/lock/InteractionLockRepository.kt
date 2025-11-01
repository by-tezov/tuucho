package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InterractionLockRepositoryProtocol
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class InteractionLockRepository(
    private val idGenerator: IdGeneratorProtocol,
) : InterractionLockRepositoryProtocol {
    private val locks = mutableMapOf<InterractionLockRepositoryProtocol.Type, String>()
    private val mutex = Mutex()

    override suspend fun tryLock(
        type: InterractionLockRepositoryProtocol.Type
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
        type: InterractionLockRepositoryProtocol.Type,
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
