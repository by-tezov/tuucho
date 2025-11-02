package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Value.Companion.toValue
import java.util.prefs.Preferences

internal class KeyValueStoreRepository(
    private val prefs: Preferences,
) : KeyValueStoreRepositoryProtocol {
    override suspend fun save(
        key: KeyValueStoreRepositoryProtocol.Key,
        value: KeyValueStoreRepositoryProtocol.Value?,
    ) {
        if (value == null) {
            prefs.remove(key.value)
        } else {
            prefs.put(key.value, value.value)
        }
    }

    override suspend fun hasKey(
        key: KeyValueStoreRepositoryProtocol.Key
    ): Boolean = prefs.get(key.value, null) != null

    override suspend fun get(
        key: KeyValueStoreRepositoryProtocol.Key
    ): KeyValueStoreRepositoryProtocol.Value = getOrNull(key)
        ?: throw DataException.Default("Key ${key.value} not found in store")

    override suspend fun getOrNull(
        key: KeyValueStoreRepositoryProtocol.Key
    ): KeyValueStoreRepositoryProtocol.Value? = prefs.get(key.value, null)?.toValue()
}
