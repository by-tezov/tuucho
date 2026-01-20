package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.interaction.imageProcessor.ImageExecutor
import com.tezov.tuucho.core.domain.business.interaction.imageProcessor.LocalImageMiddleware
import com.tezov.tuucho.core.domain.business.interaction.imageProcessor.RemoteImageMiddleware
import com.tezov.tuucho.core.domain.business.middleware.ImageMiddleware
import com.tezov.tuucho.core.domain.business.protocol.ImageExecutorProtocol
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal object ImageProcessorModule {
    fun invoke() = module(ModuleContextDomain.Middleware) {
        factory<ImageExecutorProtocol> {
            ImageExecutor(
                coroutineScopes = get(),
                middlewareExecutor = get(),
                middlewares = getAll()
            )
        }
        factoryOf(::RemoteImageMiddleware) bind ImageMiddleware::class
        factoryOf(::LocalImageMiddleware) bind ImageMiddleware::class
    }
}
