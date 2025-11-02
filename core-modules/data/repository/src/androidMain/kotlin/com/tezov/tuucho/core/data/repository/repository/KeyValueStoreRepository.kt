package com.tezov.tuucho.core.data.repository.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Value.Companion.toValue
import kotlinx.coroutines.flow.first

internal class KeyValueStoreRepository(
    private val dataStore: DataStore<Preferences>,
) : KeyValueStoreRepositoryProtocol {
    override suspend fun save(
        key: KeyValueStoreRepositoryProtocol.Key,
        value: KeyValueStoreRepositoryProtocol.Value?,
    ) {
        val prefKey = stringPreferencesKey(key.value)
        dataStore.edit { prefs ->
            if (value == null) {
                prefs.remove(prefKey)
            } else {
                prefs[prefKey] = value.value
            }
        }
    }

    override suspend fun hasKey(
        key: KeyValueStoreRepositoryProtocol.Key
    ): Boolean {
        val prefKey = stringPreferencesKey(key.value)
        val prefs = dataStore.data.first()
        return prefs.contains(prefKey)
    }

    override suspend fun get(
        key: KeyValueStoreRepositoryProtocol.Key
    ): KeyValueStoreRepositoryProtocol.Value = getOrNull(key)
        ?: throw DataException.Default("Key ${key.value} not found in store")

    override suspend fun getOrNull(
        key: KeyValueStoreRepositoryProtocol.Key
    ): KeyValueStoreRepositoryProtocol.Value? {
        val prefKey = stringPreferencesKey(key.value)
        val prefs = dataStore.data.first()
        return prefs[prefKey]?.toValue()
    }
}
