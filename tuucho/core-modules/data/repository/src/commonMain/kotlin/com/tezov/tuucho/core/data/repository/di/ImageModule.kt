package com.tezov.tuucho.core.data.repository.di

import coil3.ImageLoader
import coil3.PlatformContext
import com.tezov.tuucho.core.data.repository.image.ImageSource
import com.tezov.tuucho.core.data.repository.image.ImageSourceProtocol
import com.tezov.tuucho.core.data.repository.image.source.HttpRemoteFetcher
import com.tezov.tuucho.core.data.repository.image.source.ImageLoaderSource
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

object ImageModule {

    internal fun invoke() = module(ModuleContextData.Main) {

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
                .components {
                    add(get<HttpRemoteFetcher.Factory>())
                }
                .build()
        }

        factoryOf(HttpRemoteFetcher::Factory)
        factoryOf(::ImageLoaderSource)
        factoryOf(::ImageSource) bind ImageSourceProtocol ::class
    }
}
