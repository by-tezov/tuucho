package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.ActionExecutor
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.FormSendUrlActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.FormUpdateActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.NavigationLocalDestinationActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.NavigationUrlActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.StoreActionMiddleware
import com.tezov.tuucho.core.domain.business.protocol.ActionExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal object ActionProcessorModule {
    fun invoke() = module(ModuleContextDomain.Middleware) {
        single<ActionExecutorProtocol> {
            ActionExecutor(
                coroutineScopes = get(),
                middlewareExecutor = get(),
                middlewares = getAll(),
                interactionLockResolver = get(),
                interactionLockRegistry = get()
            )
        }
        factoryOf(::FormSendUrlActionMiddleware) bind ActionMiddlewareProtocol::class
        factoryOf(::FormUpdateActionMiddleware) bind ActionMiddlewareProtocol::class
        factoryOf(::NavigationLocalDestinationActionMiddleware) bind ActionMiddlewareProtocol::class
        factoryOf(::NavigationUrlActionMiddleware) bind ActionMiddlewareProtocol::class
        factoryOf(::StoreActionMiddleware) bind ActionMiddlewareProtocol::class
    }
}
