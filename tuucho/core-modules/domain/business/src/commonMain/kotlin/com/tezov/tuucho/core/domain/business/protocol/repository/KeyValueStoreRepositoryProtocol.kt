package com.tezov.tuucho.core.domain.business.protocol.repository

interface KeyValueStoreRepositoryProtocol {

    @JvmInline
    value class Key(val value: String) {
        companion object {
            fun String.toKey() = Key(this)
        }
    }

    interface Value {
        val valueString: kotlin.String

        @JvmInline
        value class String(val value: kotlin.String) : Value {
            override val valueString get() = value

            companion object {
                fun kotlin.String.toValue() = String(this)
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
