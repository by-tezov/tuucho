package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.di.StoreRepositoryModule.Name.STORE_REPOSITORY_CONFIG
import com.tezov.tuucho.core.data.repository.repository.KeyValueStoreRepository
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import platform.Foundation.NSUserDefaults

internal object StoreRepositoryModuleIos {
    fun invoke() = module(ModuleGroupData.Main) {
        single<NSUserDefaults> {
            NSUserDefaults(
                suiteName = get<StoreRepositoryModule.Config>(STORE_REPOSITORY_CONFIG).fileName
            )
        }

        factory<KeyValueStoreRepositoryProtocol> {
            KeyValueStoreRepository(
                userDefaults = get()
            )
        }
    }
}
