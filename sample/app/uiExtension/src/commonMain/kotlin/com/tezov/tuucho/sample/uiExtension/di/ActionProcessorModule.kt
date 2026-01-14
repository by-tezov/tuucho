package com.tezov.tuucho.sample.uiExtension.di

import com.tezov.tuucho.core.domain.business.di.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.di.ModuleContextDomain
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.sample.uiExtension.domain.EchoMessageCustomActionMiddleware
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal object ActionProcessorModule {
    fun invoke() = module(ModuleContextDomain.Middleware) {
        factoryOf(::EchoMessageCustomActionMiddleware) bind ActionMiddleware::class
    }
}
