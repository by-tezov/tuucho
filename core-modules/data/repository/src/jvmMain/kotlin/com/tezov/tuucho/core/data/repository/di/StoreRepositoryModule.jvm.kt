package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.repository.KeyValueStoreRepository
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import org.koin.core.module.Module
import java.util.prefs.Preferences

object StoreRepositoryModuleJvm {
    internal fun invoke() = object : ModuleProtocol {
        override val group = ModuleGroupData.Main

        override fun Module.declaration() {
            single<Preferences> {
                val config = get<StoreRepositoryModule.Config>()
                val safeNodeName = config.fileName.replace(Regex("[^A-Za-z0-9/]"), "")
                Preferences.userRoot().node(safeNodeName)
            }

            factory<KeyValueStoreRepositoryProtocol> {
                KeyValueStoreRepository(
                    prefs = get()
                )
            }
        }
    }
}
