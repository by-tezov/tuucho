package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.repository.KeyValueStoreRepository
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import org.koin.dsl.module

internal object StoreRepositoryModuleIos {

    fun invoke() = module {

        single<KeyValueStoreRepositoryProtocol> {
            KeyValueStoreRepository(
                userDefaults = NSUserDefaults(
                    suiteName = get<SystemCoreDataModules.Config>().localDatastoreFile
                )!!
            )
        }
    }

}