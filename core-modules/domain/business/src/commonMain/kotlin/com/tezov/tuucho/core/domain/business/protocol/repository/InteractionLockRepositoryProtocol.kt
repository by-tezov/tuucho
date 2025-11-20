package com.tezov.tuucho.core.domain.business.protocol.repository

data class InteractionLock(
    val owner: String,
    val id: String,
    val type: Type,
    val canBeReleased: Boolean = true,
) {
    enum class Type {
        ScreenInteraction,
        Navigation,
    }

    companion object {
        fun InteractionLock.freeze() = copy(canBeReleased = false)
    }
}

interface InteractionLockRepositoryProtocol {

    interface Stack {
        suspend fun isValid(
            lock: InteractionLock
        ): Boolean

        suspend fun acquire(
            requester: String,
            types: List<InteractionLock.Type>
        ): List<InteractionLock>

        suspend fun acquire(
            requester: String,
            type: InteractionLock.Type
        ): InteractionLock

        suspend fun tryAcquire(
            requester: String,
            types: List<InteractionLock.Type>
        ): List<InteractionLock>?

        suspend fun tryAcquire(
            requester: String,
            type: InteractionLock.Type
        ): InteractionLock?

        suspend fun release(
            requester: String,
            lock: InteractionLock
        )

        suspend fun release(
            requester: String,
            locks: List<InteractionLock>
        )
    }

    interface Resolver {
        fun acquire(requester: String, provider: Provider): List<InteractionLock>
        fun tryAcquire(requester: String, provider: Provider): List<InteractionLock>
        fun release(requester: String, locks: List<InteractionLock>)
    }

    interface Provider {

        operator fun get(type: InteractionLock.Type): InteractionLock? {
            return TODO()
        }

    }

}
