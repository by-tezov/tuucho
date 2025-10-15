package com.tezov.tuucho.core.data.repository.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.tezov.tuucho.core.data.repository.di.DatabaseRepositoryModuleAndroid.Name
import com.tezov.tuucho.core.data.repository.repository.KeyValueStoreRepository
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import org.koin.dsl.module

internal object StoreRepositoryModuleAndroid {

    fun invoke() = module {

        single<DataStore<Preferences>> {
            val context: Context = get(Name.APPLICATION_CONTEXT)
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