package com.tezov.tuucho.sample.uiExtension.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business._system.koin.KoinModuleExtension.factoryObject
import com.tezov.tuucho.core.domain.business.di.ModuleContextDomain
import com.tezov.tuucho.core.domain.business.protocol.ActionDefinitionProtocol
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol
import com.tezov.tuucho.sample.uiExtension.domain.EchoMessageCustomActionDefinition
import com.tezov.tuucho.sample.uiExtension.domain.EchoMessageCustomActionMiddleware
import org.koin.dsl.bind
import org.koin.plugin.module.dsl.factory

internal object ActionProcessorModule {
    fun invoke() = module(ModuleContextDomain.Middleware) {
        factory<EchoMessageCustomActionMiddleware>() bind ActionMiddlewareProtocol::class

        factoryObject(EchoMessageCustomActionDefinition) bind ActionDefinitionProtocol::class
    }
}
