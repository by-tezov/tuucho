package com.tezov.tuucho.core.data.repository.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Value.String.Companion.toValue
import kotlinx.coroutines.flow.first

internal class KeyValueStoreRepositoryAndroid(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val dataStore: DataStore<Preferences>,
    private val prefix: String?,
) : KeyValueStoreRepositoryProtocol {

    private val KeyValueStoreRepositoryProtocol.Key.withPrefix get() = prefix?.let { "$prefix-${value}" } ?: this.value

    override suspend fun save(
        key: KeyValueStoreRepositoryProtocol.Key,
        value: KeyValueStoreRepositoryProtocol.Value?,
    ) {
        coroutineScopes.io.withContext {
            val prefKey = stringPreferencesKey(key.withPrefix)
            dataStore.edit { prefs ->
                if (value == null) {
                    prefs.remove(prefKey)
                } else {
                    prefs[prefKey] = value.valueString
                }
            }
        }
    }

    override suspend fun hasKey(
        key: KeyValueStoreRepositoryProtocol.Key
    ) = coroutineScopes.io.withContext {
        val prefKey = stringPreferencesKey(key.withPrefix)
        val prefs = dataStore.data.first()
        prefs.contains(prefKey)
    }

    override suspend fun get(
        key: KeyValueStoreRepositoryProtocol.Key
    ) = getOrNull(key)
        ?: throw DataException.Default("Key ${key.withPrefix} not found in store")

    override suspend fun getOrNull(
        key: KeyValueStoreRepositoryProtocol.Key
    ) = coroutineScopes.io.withContext {
        val prefKey = stringPreferencesKey(key.withPrefix)
        val prefs = dataStore.data.first()
        prefs[prefKey]?.toValue()
    }
}
