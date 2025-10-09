package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.data.repository.di.SystemCoreDataModules
import com.tezov.tuucho.sample.app.shared.BuildKonfig
import org.koin.dsl.ModuleDeclaration

internal object ConfigModuleIos {

    fun invoke(): ModuleDeclaration = {

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
                override val resourceEndpoint = BuildKonfig.serverResourceEndpoint
                override val sendEndpoint = BuildKonfig.serverSendEndpoint
            }
        }

    }

}