package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Value.Companion.toValue
import java.util.prefs.Preferences

internal class KeyValueStoreRepositoryJvm(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val prefs: Preferences,
    private val prefix: String?,
) : KeyValueStoreRepositoryProtocol {
    private val KeyValueStoreRepositoryProtocol.Key.withPrefix get() = prefix?.let { "$prefix-$value" } ?: this.value

    override suspend fun save(
        key: KeyValueStoreRepositoryProtocol.Key,
        value: KeyValueStoreRepositoryProtocol.Value?,
    ) {
        coroutineScopes.io.withContext {
            if (value == null) {
                prefs.remove(key.withPrefix)
            } else {
                prefs.put(key.withPrefix, value.value)
            }
        }
    }

    override suspend fun hasKey(
        key: KeyValueStoreRepositoryProtocol.Key
    ) = coroutineScopes.io.withContext {
        prefs.get(key.withPrefix, null) != null
    }

    override suspend fun get(
        key: KeyValueStoreRepositoryProtocol.Key
    ) = getOrNull(key)
        ?: throw DataException.Default("Key ${key.withPrefix} not found in store")

    override suspend fun getOrNull(
        key: KeyValueStoreRepositoryProtocol.Key
    ) = coroutineScopes.io.withContext {
        prefs.get(key.withPrefix, null)?.toValue()
    }
}
