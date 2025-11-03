package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.repository.KeyValueStoreRepository
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import org.koin.core.module.Module
import platform.Foundation.NSUserDefaults

internal object StoreRepositoryModuleIos {
    fun invoke() = object : ModuleProtocol {
        override val group = ModuleGroupData.Main

        override fun Module.declaration() {
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
}
