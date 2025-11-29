package com.tezov.tuucho.core.data.repository.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.tezov.tuucho.core.data.repository.di.StoreRepositoryModule.Name.STORE_REPOSITORY_CONFIG
import com.tezov.tuucho.core.data.repository.di.SystemCoreDataModulesAndroid.Name.APPLICATION_CONTEXT
import com.tezov.tuucho.core.data.repository.repository.KeyValueStoreRepository
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi

internal object StoreRepositoryModuleAndroid {
    fun invoke() = module(ModuleGroupData.Main) {
        @OptIn(TuuchoInternalApi::class)
        single<DataStore<Preferences>> {
            val context: Context = get(APPLICATION_CONTEXT)
            PreferenceDataStoreFactory.create(
                scope = get<CoroutineScopesProtocol>().io.scope,
                produceFile = {
                    context.preferencesDataStoreFile(
                        get<StoreRepositoryModule.Config>(STORE_REPOSITORY_CONFIG).fileName
                    )
                }
            )
        }

        factory<KeyValueStoreRepositoryProtocol> {
            KeyValueStoreRepository(
                dataStore = get(),
            )
        }
    }
}
