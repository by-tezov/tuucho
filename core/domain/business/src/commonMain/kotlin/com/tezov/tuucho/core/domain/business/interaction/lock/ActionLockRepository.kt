package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ActionLockRepositoryProtocol

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ActionLockRepository(
    private val idGenerator: IdGeneratorProtocol,
) : ActionLockRepositoryProtocol {

    private val locks = mutableMapOf<ActionLockRepositoryProtocol.Type, String>()
    private val mutex = Mutex()

    override suspend fun tryLock(type: ActionLockRepositoryProtocol.Type): String? {
        return mutex.withLock {
            if (locks.containsKey(type)) {
                null
            } else {
                idGenerator.generate().also {
                    locks[type] = it
                }
            }
        }
    }

    override suspend fun unLock(type: ActionLockRepositoryProtocol.Type, handle: String) {
        mutex.withLock {
            val current = locks[type]
            if (current == null || current != handle) {
                throw DomainException.Default("Invalid unlock handle for $type")
            }
            locks.remove(type)
        }
    }
}
