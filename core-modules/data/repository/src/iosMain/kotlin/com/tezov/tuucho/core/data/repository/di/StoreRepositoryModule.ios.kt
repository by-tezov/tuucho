package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.repository.KeyValueStoreRepository
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

internal object StoreRepositoryModuleIos {

    fun invoke() = module {

        single<NSUserDefaults> {
            NSUserDefaults(
                suiteName = get<StoreRepositoryModule.Config>().fileName
            )
        }

        factory<KeyValueStoreRepositoryProtocol> {
            KeyValueStoreRepository(
                userDefaults = get()
            )
        }
    }

}