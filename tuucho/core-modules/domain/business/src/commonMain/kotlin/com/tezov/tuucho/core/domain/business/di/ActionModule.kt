package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business._system.koin.KoinModuleExtension.factoryObject
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.ActionMiddlewareExecutor
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.FormSendUrlActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.FormUpdateActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.LanguageActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.NavigationLocalDestinationActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.NavigationUrlActionMiddleware
import com.tezov.tuucho.core.domain.business.interaction.actionMiddleware.StoreActionMiddleware
import com.tezov.tuucho.core.domain.business.model.action.FormActionDefinition
import com.tezov.tuucho.core.domain.business.model.action.NavigateActionDefinition
import com.tezov.tuucho.core.domain.business.model.action.StoreActionDefinition
import com.tezov.tuucho.core.domain.business.protocol.ActionDefinitionProtocol
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal object ActionModule {
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

        factoryObject(NavigateActionDefinition.Url) bind ActionDefinitionProtocol::class
        factoryObject(NavigateActionDefinition.LocalDestination) bind ActionDefinitionProtocol::class
        factoryObject(FormActionDefinition.Send) bind ActionDefinitionProtocol::class
        factoryObject(FormActionDefinition.Update) bind ActionDefinitionProtocol::class
        factoryObject(StoreActionDefinition.KeyValue) bind ActionDefinitionProtocol::class

        factoryOf(::FormSendUrlActionMiddleware) bind ActionMiddlewareProtocol::class
        factoryOf(::FormUpdateActionMiddleware) bind ActionMiddlewareProtocol::class
        factoryOf(::NavigationLocalDestinationActionMiddleware) bind ActionMiddlewareProtocol::class
        factoryOf(::NavigationUrlActionMiddleware) bind ActionMiddlewareProtocol::class
        factoryOf(::StoreActionMiddleware) bind ActionMiddlewareProtocol::class
        factoryOf(::LanguageActionMiddleware) bind ActionMiddlewareProtocol::class
    }
}
