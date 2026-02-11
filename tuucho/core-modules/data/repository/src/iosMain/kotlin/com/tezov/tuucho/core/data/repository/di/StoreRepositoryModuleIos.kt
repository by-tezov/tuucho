package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.di.StoreRepositoryModule.Name.STORE_REPOSITORY_CONFIG
import com.tezov.tuucho.core.data.repository.repository.KeyValueStoreRepositoryIos
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import platform.Foundation.NSUserDefaults

internal object StoreRepositoryModuleIos {
    fun invoke() = module(ModuleContextData.Main) {
        single<NSUserDefaults> {
            NSUserDefaults(
                suiteName = get<StoreRepositoryModule.Config>(STORE_REPOSITORY_CONFIG).fileName
            )
        }

        factory<KeyValueStoreRepositoryProtocol> { params ->
            KeyValueStoreRepositoryIos(
                coroutineScopes = get(),
                userDefaults = get(),
                prefix = params.getOrNull()
            )
        }
    }
}
