package com.tezov.tuucho.core.data.repository.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.tezov.tuucho.core.data.repository.di.StoreRepositoryModule.Name.STORE_REPOSITORY_CONFIG
import com.tezov.tuucho.core.data.repository.di.SystemCoreDataModulesAndroid.Name.APPLICATION_CONTEXT
import com.tezov.tuucho.core.data.repository.repository.KeyValueStoreRepository
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import org.koin.dsl.bind
import org.koin.dsl.onClose
import org.koin.plugin.module.dsl.factory

internal object StoreRepositoryModuleAndroid {
    private var datastore: DataStore<Preferences>? = null

    fun invoke() = module(ModuleContextData.Main) {
        @OptIn(TuuchoInternalApi::class)
        single<DataStore<Preferences>> {
            datastore ?: run {
                val context: Context = get(APPLICATION_CONTEXT)
                PreferenceDataStoreFactory
                    .create(
                        scope = get<CoroutineScopesProtocol>().io.scope,
                        produceFile = {
                            context.preferencesDataStoreFile(
                                get<StoreRepositoryModule.Config>(STORE_REPOSITORY_CONFIG).fileName
                            )
                        }
                    ).also { datastore = it }
            }
        } onClose {
            // datastore = null, doesn't work, when datastore restart, application crash. For now, I keep it alive forever...
        }

        factory<KeyValueStoreRepository>() bind KeyValueStoreRepositoryProtocol::class
    }
}
