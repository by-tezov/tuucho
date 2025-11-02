package com.tezov.tuucho.core.data.repository.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.tezov.tuucho.core.data.repository.repository.KeyValueStoreRepository
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import org.koin.core.module.Module

internal object StoreRepositoryModuleAndroid {
    fun invoke() = object : ModuleProtocol {
        override val group = ModuleGroupData.Main

        override fun Module.declaration() {
            @OptIn(TuuchoInternalApi::class)
            single<DataStore<Preferences>> {
                val context: Context = get(SystemCoreDataModulesAndroid.Name.APPLICATION_CONTEXT)
                PreferenceDataStoreFactory.create(
                    scope = get<CoroutineScopesProtocol>().datastore.scope,
                    produceFile = {
                        context.preferencesDataStoreFile(
                            get<StoreRepositoryModule.Config>().fileName
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
}
