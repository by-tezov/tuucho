package com.tezov.tuucho.core.data.repository.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.tezov.tuucho.core.data.repository.di.DatabaseRepositoryModuleAndroid.Name
import com.tezov.tuucho.core.data.repository.repository.KeyValueStoreRepository
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

internal object StoreRepositoryModuleAndroid {

    fun invoke() = module {

        single<DataStore<Preferences>> {
            val context: Context = get(Name.APPLICATION_CONTEXT)
            PreferenceDataStoreFactory.create(
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
            ) {
                context.preferencesDataStoreFile(
                    get<SystemCoreDataModules.Config>().localDatastoreFile
                )
            }
        }


        factory<KeyValueStoreRepositoryProtocol> {
            KeyValueStoreRepository(
                dataStore = get(),
            )
        }
    }

}