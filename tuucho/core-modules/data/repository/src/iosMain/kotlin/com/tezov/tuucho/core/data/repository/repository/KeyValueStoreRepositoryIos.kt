package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Value.Companion.toValue
import platform.Foundation.NSUserDefaults

class KeyValueStoreRepositoryIos(
    private val userDefaults: NSUserDefaults,
    private val prefix: String?,
) : KeyValueStoreRepositoryProtocol {

    private val KeyValueStoreRepositoryProtocol.Key.withPrefix get() = prefix?.let { "$prefix-${value}" } ?: this.value

    override suspend fun save(
        key: KeyValueStoreRepositoryProtocol.Key,
        value: KeyValueStoreRepositoryProtocol.Value?,
    ) {
        if (value == null) {
            userDefaults.removeObjectForKey(key.withPrefix)
        } else {
            userDefaults.setObject(value.value, forKey = key.withPrefix)
        }
    }

    override suspend fun hasKey(
        key: KeyValueStoreRepositoryProtocol.Key
    ): Boolean = userDefaults.objectForKey(key.withPrefix) != null

    override suspend fun get(
        key: KeyValueStoreRepositoryProtocol.Key
    ): KeyValueStoreRepositoryProtocol.Value = getOrNull(key)
        ?: throw DataException.Default("Key ${key.withPrefix} not found in store")

    override suspend fun getOrNull(
        key: KeyValueStoreRepositoryProtocol.Key
    ): KeyValueStoreRepositoryProtocol.Value? {
        val stored = userDefaults.stringForKey(key.withPrefix)
        return stored?.toValue()
    }
}
