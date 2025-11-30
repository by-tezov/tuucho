package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.ActionExecutor
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.FormSendUrlActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.FormUpdateActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.NavigationLocalDestinationActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.NavigationUrlActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.StoreActionMiddleware
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.protocol.ActionExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import org.koin.dsl.bind

internal object ActionProcessorModule {
    fun invoke() = module(ModuleGroupDomain.Middleware) {
        factory<ActionExecutorProtocol> {
            ActionExecutor(
                coroutineScopes = get(),
                middlewareExecutor = get(),
                middlewares = getAll(),
                interactionLockResolver = get(),
                interactionLockRegistry = get()
            )
        }

        factory<FormSendUrlActionMiddleware> {
            FormSendUrlActionMiddleware(
                useCaseExecutor = get(),
                getScreenOrNull = get(),
                sendData = get()
            )
        } bind ActionMiddleware::class

        factory<FormUpdateActionMiddleware> {
            FormUpdateActionMiddleware(
                useCaseExecutor = get(),
                updateView = get()
            )
        } bind ActionMiddleware::class

        factory<NavigationLocalDestinationActionMiddleware> {
            NavigationLocalDestinationActionMiddleware(
                useCaseExecutor = get(),
                navigateBack = get()
            )
        } bind ActionMiddleware::class

        factory<NavigationUrlActionMiddleware> {
            NavigationUrlActionMiddleware(
                useCaseExecutor = get(),
                navigateToUrl = get()
            )
        } bind ActionMiddleware::class

        factory<StoreActionMiddleware> {
            StoreActionMiddleware(
                useCaseExecutor = get(),
                saveKeyValueToStore = get(),
                removeKeyValueFromStore = get()
            )
        } bind ActionMiddleware::class
    }
}
