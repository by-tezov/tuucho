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
        requester: String,
        types: List<Type>
    ): Lock

    suspend fun tryAcquire(
        requester: String,
        types: List<Type>
    ): Lock?

    suspend fun acquire(
        requester: String,
        type: Type
    ): Lock.Element

    suspend fun tryAcquire(
        requester: String,
        type: Type
    ): Lock.Element?

    suspend fun release(
        requester: String,
        lock: Lock
    )
}
