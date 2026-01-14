package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.domain.business.di.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.di.ModuleContextDomain
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.shared.sample.action.LoggerAction
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

object InteractionModule {

    fun invoke() = module(ModuleContextDomain.Middleware) {
        factoryOf(::LoggerAction) bind ActionMiddleware::class
    }

}
