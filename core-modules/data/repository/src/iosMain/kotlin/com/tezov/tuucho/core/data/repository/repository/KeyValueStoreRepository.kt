package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Value.Companion.toValue
import platform.Foundation.NSUserDefaults

class KeyValueStoreRepository(
    private val userDefaults: NSUserDefaults,
) : KeyValueStoreRepositoryProtocol {

    override suspend fun save(
        key: KeyValueStoreRepositoryProtocol.Key,
        value: KeyValueStoreRepositoryProtocol.Value?,
    ) {
        if (value == null) {
            userDefaults.removeObjectForKey(key.value)
        } else {
            userDefaults.setObject(value.value, forKey = key.value)
        }
    }

    override suspend fun hasKey(key: KeyValueStoreRepositoryProtocol.Key): Boolean {
        return userDefaults.objectForKey(key.value) != null
    }

    override suspend fun get(key: KeyValueStoreRepositoryProtocol.Key): KeyValueStoreRepositoryProtocol.Value {
        return getOrNull(key)
            ?: throw DataException.Default("Key ${key.value} not found in store")
    }

    override suspend fun getOrNull(key: KeyValueStoreRepositoryProtocol.Key): KeyValueStoreRepositoryProtocol.Value? {
        val stored = userDefaults.stringForKey(key.value)
        return stored?.toValue()
    }

}
