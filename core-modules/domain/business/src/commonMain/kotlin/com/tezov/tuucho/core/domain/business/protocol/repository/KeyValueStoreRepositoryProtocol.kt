package com.tezov.tuucho.core.domain.business.protocol.repository

interface KeyValueStoreRepositoryProtocol {

    interface Key {
        val value: String

        companion object {
            fun String.toKey() = object : Key {
                override val value: String = this@toKey
            }
        }
    }

    interface Value {
        val value: String

        companion object {
            fun String.toValue() = object : Value {
                override val value: String = this@toValue
            }
        }
    }

    suspend fun save(key: Key, value: Value?)

    suspend fun hasKey(key: Key): Boolean

    suspend fun get(key: Key): Value

    suspend fun getOrNull(key: Key): Value?

}