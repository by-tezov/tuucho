package com.tezov.tuucho.core.domain.business.protocol.repository

interface KeyValueStoreRepositoryProtocol {
    interface Key {
        val value: String

        companion object {
            fun String.toKey() = object : Key {
                override val value: String = this@toKey

                override fun equals(
                    other: Any?
                ) = other is Key && other.value == value

                override fun hashCode() = value.hashCode()

                override fun toString() = "Key($value)"
            }
        }
    }

    interface Value {
        val value: String

        companion object {
            fun String.toValue() = object : Value {
                override val value: String = this@toValue

                override fun equals(
                    other: Any?
                ) = other is Value && other.value == value

                override fun hashCode() = value.hashCode()

                override fun toString() = "Value($value)"
            }
        }
    }

    suspend fun save(
        key: Key,
        value: Value?
    )

    suspend fun hasKey(
        key: Key
    ): Boolean

    suspend fun get(
        key: Key
    ): Value

    suspend fun getOrNull(
        key: Key
    ): Value?
}
