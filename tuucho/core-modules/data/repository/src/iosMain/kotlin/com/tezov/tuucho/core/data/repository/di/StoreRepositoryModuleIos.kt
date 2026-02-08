package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.di.StoreRepositoryModule.Name.STORE_REPOSITORY_CONFIG
import com.tezov.tuucho.core.data.repository.repository.KeyValueStoreRepositoryIos
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import org.koin.dsl.onClose
import platform.Foundation.NSUserDefaults

internal object StoreRepositoryModuleIos {
    private var datastore: NSUserDefaults? = null

    fun invoke() = module(ModuleContextData.Main) {
        single<NSUserDefaults> {
            datastore ?: run {
                NSUserDefaults(
                    suiteName = get<StoreRepositoryModule.Config>(STORE_REPOSITORY_CONFIG).fileName
                )
            }.also { datastore = it }
        } onClose {
            // datastore = null, doesn't work for android, to be consistent I don't null it here too.
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
