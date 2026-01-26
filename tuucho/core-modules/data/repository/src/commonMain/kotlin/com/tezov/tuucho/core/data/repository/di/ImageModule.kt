package com.tezov.tuucho.core.data.repository.di

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.fetch.Fetcher
import com.tezov.tuucho.core.data.repository.image.ImageFetcherRegistry
import com.tezov.tuucho.core.data.repository.image.ImageFetcherRegistryProtocol
import com.tezov.tuucho.core.data.repository.image.ImageLoaderSource
import com.tezov.tuucho.core.data.repository.image.ImageLocalFetcher
import com.tezov.tuucho.core.data.repository.image.ImageRemoteFetcher
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.model.image.LocalImageDefinition
import com.tezov.tuucho.core.domain.business.model.image.RemoteImageDefinition
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind

object ImageModule {
    internal operator fun invoke() = module(ModuleContextData.Main) {
        single<ImageLoader> {
            val platformContext = get<PlatformContext>()
            ImageLoader
                .Builder(context = platformContext)
//                .memoryCache {
//                    MemoryCache.Builder()
//                        .maxSizePercent(platformContext, 0.25)
//                        .build()
//                }
//                .diskCache {
//                    DiskCache.Builder()
//                        .directory(platformContext.cacheDir.resolve("image_cache"))
//                        .maxSizePercent(0.02)
//                        .build()
//                }
                .build()
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
                httpClient = get(),
                config = get()
            )
        } bind Fetcher.Factory::class
        factory(named(LocalImageDefinition.command)) {
            ImageLocalFetcher.Factory(
                assetSource = get(),
            )
        } bind Fetcher.Factory::class
    }
}
