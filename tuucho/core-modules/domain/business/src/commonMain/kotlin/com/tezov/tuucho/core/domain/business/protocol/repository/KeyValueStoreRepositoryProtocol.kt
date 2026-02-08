package com.tezov.tuucho.core.domain.business.protocol.repository

import kotlin.jvm.JvmInline

interface KeyValueStoreRepositoryProtocol {
    @JvmInline
    value class Key(
        val value: String
    ) {
        companion object {
            fun String.toKey() = Key(this)
        }
    }

    @JvmInline
    value class Value(
        val value: String
    ) {
        companion object {
            fun String.toValue() = Value(this)
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
