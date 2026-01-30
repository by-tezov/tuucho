package com.tezov.tuucho.core.domain.business.protocol.repository

abstract class InteractionLockType(
    val name: String
) {
    object Screen : InteractionLockType("screen")

    object Navigation : InteractionLockType("navigation")

    override fun toString() = name
}

data class InteractionLock(
    val owner: String,
    val id: String,
    val type: InteractionLockType,
    val canBeReleased: Boolean = true,
) {
    fun freeze() = copy(canBeReleased = false)
}

sealed class InteractionLockable {
    data object Empty : InteractionLockable()

    data class Type(
        val value: List<InteractionLockType>
    ) : InteractionLockable()

    data class Lock(
        val value: List<InteractionLock>
    ) : InteractionLockable()

    data class Composite(
        val type: Type,
        val lock: Lock
    ) : InteractionLockable()

    operator fun get(
        type: InteractionLockType
    ): InteractionLock? = when (this) {
        is Lock -> value.firstOrNull { it.type == type }
        is Composite -> lock.value.firstOrNull { it.type == type }
        else -> null
    }

    fun getTypes(): List<InteractionLockType> = when (this) {
        is Type -> value
        is Composite -> type.value
        else -> emptyList()
    }

    fun getLocks(): List<InteractionLock> = when (this) {
        is Lock -> value
        is Composite -> lock.value
        else -> emptyList()
    }

    fun freeze(): InteractionLockable = when (this) {
        is Lock -> Lock(value.map { it.freeze() })
        is Composite -> Composite(type, Lock(lock.value.map { it.freeze() }))
        else -> this
    }

    private fun mergeTypes(
        a: Type,
        b: Type
    ): Type = Type((a.value + b.value).distinct())

    private fun mergeLocks(
        a: Lock,
        b: Lock
    ): Lock = Lock((a.value + b.value).distinct())

    operator fun plus(
        other: InteractionLockable
    ): InteractionLockable {
        if (this is Empty) return other
        if (other is Empty) return this
        return when (this) {
            is Type -> when (other) {
                is Type -> mergeTypes(this, other)
                is Lock -> Composite(this, other)
                is Composite -> Composite(mergeTypes(this, other.type), other.lock)
            }

            is Lock -> when (other) {
                is Lock -> mergeLocks(this, other)
                is Type -> Composite(other, this)
                is Composite -> Composite(other.type, mergeLocks(this, other.lock))
            }

            is Composite -> when (other) {
                is Type -> Composite(mergeTypes(type, other), lock)

                is Lock -> Composite(type, mergeLocks(lock, other))

                is Composite -> Composite(
                    mergeTypes(type, other.type),
                    mergeLocks(lock, other.lock)
                )
            }
        }
    }
}

object InteractionLockProtocol {
    interface Stack {
        suspend fun isValid(
            lock: InteractionLock
        ): Boolean

        suspend fun isAllValid(
            locks: List<InteractionLock>
        ): Boolean

        suspend fun acquire(
            requester: String,
            types: List<InteractionLockType>
        ): List<InteractionLock>

        suspend fun acquire(
            requester: String,
            type: InteractionLockType
        ): InteractionLock

        suspend fun tryAcquire(
            requester: String,
            types: List<InteractionLockType>
        ): List<InteractionLock>?

        suspend fun tryAcquire(
            requester: String,
            type: InteractionLockType
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
        suspend fun acquire(
            requester: String,
            lockable: InteractionLockable
        ): InteractionLockable

        suspend fun tryAcquire(
            requester: String,
            lockable: InteractionLockable
        ): InteractionLockable

        suspend fun release(
            requester: String,
            lockable: InteractionLockable
        )
    }

    interface Registry {
        fun lockTypeFor(
            command: String,
            authority: String?
        ): InteractionLockable
    }
}
