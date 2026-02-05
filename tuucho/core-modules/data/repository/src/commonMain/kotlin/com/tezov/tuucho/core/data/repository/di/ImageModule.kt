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
import org.koin.dsl.bind
import org.koin.plugin.module.dsl.factory
import org.koin.plugin.module.dsl.single

object ImageModule {
    interface Config {
        val diskCacheSizeMo: Int?
        val diskCacheDirectory: String?
    }

    internal operator fun invoke() = module(ModuleContextData.Main) {
        single<ImageDiskCache>() bind ImageDiskCacheProtocol::class

        single {
            ImageLoader.Builder(context = get<PlatformContext>()).build()
        }

        factory<ImageLoaderSource>()

        single<ImageFetcherRegistryProtocol> {
            ImageFetcherRegistry(factories = getAll())
        }

        factory<ImageRemoteFetcher.Factory>() bind ImageFetcherProtocol.Factory::class
        factory<ImageLocalFetcher.Factory>() bind ImageFetcherProtocol.Factory::class
    }
}
