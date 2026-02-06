package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.repository.SystemPlatformRepository
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.SystemPlatformRepositoryProtocol
import org.koin.core.parameter.parametersOf

object PlatformModule {

    internal fun invoke() = module(ModuleContextData.Main) {
        factory<SystemPlatformRepositoryProtocol> {
            SystemPlatformRepository(
                keyValueStorage = get<KeyValueStoreRepositoryProtocol>(
                    parameters = { parametersOf(get<StoreRepositoryModule.Config>().prefixStore) }
                ),
                systemPlatformInformation = get()
            )
        }
    }
}
