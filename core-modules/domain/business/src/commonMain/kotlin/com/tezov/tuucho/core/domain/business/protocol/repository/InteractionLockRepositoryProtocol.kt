package com.tezov.tuucho.core.domain.business.protocol.repository

import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockGenerator.Lock

interface InteractionLockRepositoryProtocol {
    enum class Type {
        ScreenInteraction,
        Navigation,
    }

    suspend fun isValid(
        lock: Lock.Element
    ): Boolean

    suspend fun acquire(
        types: List<Type>
    ): Lock

    suspend fun tryAcquire(
        types: List<Type>
    ): Lock?

    suspend fun acquire(
        type: Type
    ): Lock.Element

    suspend fun tryAcquire(
        type: Type
    ): Lock.Element?

    suspend fun release(
        lock: Lock
    )
}
