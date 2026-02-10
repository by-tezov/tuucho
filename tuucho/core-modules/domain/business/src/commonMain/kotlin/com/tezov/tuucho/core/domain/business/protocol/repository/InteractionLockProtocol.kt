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

    data class Types(
        val values: List<InteractionLockType>
    ) : InteractionLockable()

    data class Locks(
        val values: List<InteractionLock>
    ) : InteractionLockable()

    data class Composite(
        val types: Types,
        val locks: Locks
    ) : InteractionLockable()

    operator fun get(
        type: InteractionLockType
    ): InteractionLock? = when (this) {
        is Locks -> values.firstOrNull { it.type == type }
        is Composite -> locks.values.firstOrNull { it.type == type }
        else -> null
    }

    fun getTypes(): List<InteractionLockType> = when (this) {
        is Types -> values
        is Composite -> types.values
        else -> emptyList()
    }

    fun getLocks(): List<InteractionLock> = when (this) {
        is Locks -> values
        is Composite -> locks.values
        else -> emptyList()
    }

    fun freeze(): InteractionLockable = when (this) {
        is Locks -> Locks(values.map { it.freeze() })
        is Composite -> Composite(types, Locks(locks.values.map { it.freeze() }))
        else -> this
    }

    private fun mergeTypes(
        a: Types,
        b: Types
    ): Types = Types((a.values + b.values).distinct())

    private fun mergeLocks(
        a: Locks,
        b: Locks
    ): Locks = Locks((a.values + b.values).distinct())

    operator fun plus(
        other: InteractionLockable
    ): InteractionLockable {
        if (this is Empty) return other
        if (other is Empty) return this
        return when (this) {
            is Types -> when (other) {
                is Types -> mergeTypes(this, other)
                is Locks -> Composite(this, other)
                is Composite -> Composite(mergeTypes(this, other.types), other.locks)
            }

            is Locks -> when (other) {
                is Locks -> mergeLocks(this, other)
                is Types -> Composite(other, this)
                is Composite -> Composite(other.types, mergeLocks(this, other.locks))
            }

            is Composite -> when (other) {
                is Types -> Composite(mergeTypes(types, other), locks)

                is Locks -> Composite(types, mergeLocks(locks, other))

                is Composite -> Composite(
                    mergeTypes(types, other.types),
                    mergeLocks(locks, other.locks)
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
