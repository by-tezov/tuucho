package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.domain.business.di.ModuleGroupDomain
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.shared.sample.action.LoggerAction
import org.koin.dsl.bind

object InteractionModule {

    fun invoke() = module(ModuleGroupDomain.Middleware) {
        factory<LoggerAction> {
            LoggerAction(
                logger = get(),
                systemInformation = get()
            )
        }  bind ActionMiddleware::class

    }

}
