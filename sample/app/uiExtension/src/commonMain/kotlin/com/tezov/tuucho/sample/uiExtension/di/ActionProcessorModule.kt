package com.tezov.tuucho.sample.uiExtension.di

import com.tezov.tuucho.core.domain.business._system.koin.Associate.associate
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business._system.koin.KoinModuleExtension.factoryObject
import com.tezov.tuucho.core.domain.business.di.ModuleContextDomain
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.action.NavigateActionDefinition
import com.tezov.tuucho.core.domain.business.protocol.ActionDefinitionProtocol
import com.tezov.tuucho.sample.uiExtension.domain.EchoMessageCustomActionDefinition
import com.tezov.tuucho.sample.uiExtension.domain.EchoMessageCustomActionMiddleware
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind

internal object ActionProcessorModule {
    fun invoke() = module(ModuleContextDomain.Middleware) {
        factoryOf(::EchoMessageCustomActionMiddleware) bind ActionMiddleware::class

        factoryObject(EchoMessageCustomActionDefinition) bind ActionDefinitionProtocol::class
    }
}
