package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.ActionExecutor
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.FormSendUrlActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.FormUpdateActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.NavigationLocalDestinationActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.NavigationUrlActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.StoreActionMiddleware
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.protocol.ActionExecutorProtocol
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
        factoryOf(::FormSendUrlActionMiddleware) bind ActionMiddleware::class
        factoryOf(::FormUpdateActionMiddleware) bind ActionMiddleware::class
        factoryOf(::NavigationLocalDestinationActionMiddleware) bind ActionMiddleware::class
        factoryOf(::NavigationUrlActionMiddleware) bind ActionMiddleware::class
        factoryOf(::StoreActionMiddleware) bind ActionMiddleware::class
    }
}
