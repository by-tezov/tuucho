package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.ActionMiddlewareExecutor
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.FormSendUrlActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.FormUpdateActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.LanguageActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.NavigationLocalDestinationActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.NavigationUrlActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.StoreActionMiddleware
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol
import org.koin.dsl.bind
import org.koin.plugin.module.dsl.factory

internal object ActionMiddlewareModule {
    fun invoke() = module(ModuleContextDomain.Middleware) {
        single<ActionMiddlewareExecutorProtocol> {
            ActionMiddlewareExecutor(
                coroutineScopes = get(),
                middlewareExecutor = get(),
                middlewares = getAll(),
                interactionLockResolver = get(),
                interactionLockRegistry = get()
            )
        }

        factory<FormSendUrlActionMiddleware>() bind ActionMiddlewareProtocol::class
        factory<FormUpdateActionMiddleware>() bind ActionMiddlewareProtocol::class
        factory<NavigationLocalDestinationActionMiddleware>() bind ActionMiddlewareProtocol::class
        factory<NavigationUrlActionMiddleware>() bind ActionMiddlewareProtocol::class
        factory<StoreActionMiddleware>() bind ActionMiddlewareProtocol::class
        factory<LanguageActionMiddleware>() bind ActionMiddlewareProtocol::class
    }
}
