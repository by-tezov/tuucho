package com.tezov.tuucho.sample.shared.di

import com.tezov.tuucho.core.barrel.di.ModuleContextCore
import com.tezov.tuucho.core.data.repository.di.DatabaseModule
import com.tezov.tuucho.core.data.repository.di.ImageModule
import com.tezov.tuucho.core.data.repository.di.NetworkModule
import com.tezov.tuucho.core.data.repository.di.StoreRepositoryModule
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.sample.app.shared.BuildKonfig

internal object ConfigModuleAndroid {

    fun invoke() = module(ModuleContextCore.Main) {

        factory<StoreRepositoryModule.Config> {
            object : StoreRepositoryModule.Config {
                override val fileName = BuildKonfig.localDatastoreFileName
            }
        }

        factory<DatabaseModule.Config> {
            object : DatabaseModule.Config {
                override val fileName = BuildKonfig.localDatabaseFileName
            }
        }

        factory<ImageModule.Config> {
            object : ImageModule.Config {
                override val diskCacheSizeMo = BuildKonfig.imageDiskCacheSizeMo
                override val diskCacheDirectory = BuildKonfig.imageDiskCacheDirectory
            }
        }

        factory<NetworkModule.Config> {
            object : NetworkModule.Config {
                override val jsonRequestTimeoutMillis = BuildKonfig.serverJsonTimeoutMillis
                override val imageRequestTimeoutMillis = BuildKonfig.serverImageTimeoutMillis
                override val version = BuildKonfig.serverVersion
                override val baseUrl = BuildKonfig.serverBaseUrl
                override val healthEndpoint = BuildKonfig.serverHealthEndpoint
                override val resourceEndpoint = BuildKonfig.serverResourceEndpoint
                override val sendEndpoint = BuildKonfig.serverSendEndpoint
                override val imageEndpoint = BuildKonfig.serverImageEndpoint
            }
        }

        factory<InterceptorModule.Config> {
            object : InterceptorModule.Config {
                override val headerPlatform = BuildKonfig.headerPlatform
            }
        }

    }
}
