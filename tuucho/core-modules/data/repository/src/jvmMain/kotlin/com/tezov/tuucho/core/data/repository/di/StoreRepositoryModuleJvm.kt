package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.repository.KeyValueStoreRepositoryJvm
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import java.util.prefs.Preferences

internal object StoreRepositoryModuleJvm {
    fun invoke() = module(ModuleContextData.Main) {
        @OptIn(TuuchoInternalApi::class)
        single<Preferences> {
            val config = get<StoreRepositoryModule.Config>()
            val safeNodeName = config.fileName.replace(Regex("[^A-Za-z0-9/]"), "")
            Preferences.userRoot().node(safeNodeName)
        }

        factory<KeyValueStoreRepositoryProtocol> { params ->
            KeyValueStoreRepositoryJvm(
                coroutineScopes = get(),
                prefs = get(),
                prefix = params.getOrNull()
            )
        }
    }
}
