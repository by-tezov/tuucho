package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.barrel.di.ModuleGroupCore
import com.tezov.tuucho.core.data.repository.di.DatabaseRepositoryModule
import com.tezov.tuucho.core.data.repository.di.NetworkRepositoryModule
import com.tezov.tuucho.core.data.repository.di.StoreRepositoryModule
import com.tezov.tuucho.sample.app.shared.BuildKonfig
import org.koin.dsl.ModuleDeclaration
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol

internal object ConfigModuleIos {

    fun invoke() = object : ModuleProtocol {

        override val group = ModuleGroupCore.Main

        override fun Module.declaration() {

        factory<StoreRepositoryModule.Config> {
            object : StoreRepositoryModule.Config {
                override val fileName = BuildKonfig.localDatastoreFileName
            }
        }

        factory<DatabaseRepositoryModule.Config> {
            object : DatabaseRepositoryModule.Config {
                override val fileName = BuildKonfig.localDatabaseFileName
            }
        }

        factory<NetworkRepositoryModule.Config> {
            object : NetworkRepositoryModule.Config {
                override val timeoutMillis = BuildKonfig.serverTimeoutMillis
                override val version = BuildKonfig.serverVersion
                override val baseUrl = BuildKonfig.serverBaseUrl
                override val healthEndpoint = BuildKonfig.serverHealthEndpoint
                override val resourceEndpoint = BuildKonfig.serverResourceEndpoint
                override val sendEndpoint = BuildKonfig.serverSendEndpoint
            }
        }

        factory<NetworkModule.Config> {
            object : NetworkModule.Config {
                override val headerPlatform = BuildKonfig.headerPlatform
            }
        }

    }

}