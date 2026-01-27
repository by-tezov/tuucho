package com.tezov.tuucho.core.data.repository.di

import coil3.ImageLoader
import coil3.PlatformContext
import com.tezov.tuucho.core.data.repository.image.ImageDiskCache
import com.tezov.tuucho.core.data.repository.image.ImageDiskCacheProtocol
import com.tezov.tuucho.core.data.repository.image.ImageFetcherProtocol
import com.tezov.tuucho.core.data.repository.image.ImageFetcherRegistry
import com.tezov.tuucho.core.data.repository.image.ImageFetcherRegistryProtocol
import com.tezov.tuucho.core.data.repository.image.ImageLoaderSource
import com.tezov.tuucho.core.data.repository.image.ImageLocalFetcher
import com.tezov.tuucho.core.data.repository.image.ImageRemoteFetcher
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.model.image.LocalImageDefinition
import com.tezov.tuucho.core.domain.business.model.image.RemoteImageDefinition
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind

object ImageModule {

    interface Config {
        val diskCacheSizeMo: Int?
        val diskCacheDirectory: String?
    }

    internal operator fun invoke() = module(ModuleContextData.Main) {

        singleOf(::ImageDiskCache) bind ImageDiskCacheProtocol::class

        single {
            ImageLoader.Builder(context = get<PlatformContext>()).build()
        }

        factoryOf(::ImageLoaderSource)

        single<ImageFetcherRegistryProtocol> {
            ImageFetcherRegistry().apply {
                register(RemoteImageDefinition.command)
                register(LocalImageDefinition.command)
            }
        }

        factory(named(RemoteImageDefinition.command)) {
            ImageRemoteFetcher.Factory(
                config = get(),
                httpClient = get(),
                diskCache = get(),
            )
        } bind ImageFetcherProtocol.Factory::class
        factory(named(LocalImageDefinition.command)) {
            ImageLocalFetcher.Factory(
                assetSource = get(),
            )
        } bind ImageFetcherProtocol.Factory::class
    }
}
