package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.domain.business.di.ModuleGroupDomain
import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.tool.extension.ExtensionKoin.bindOrdered
import com.tezov.tuucho.shared.sample.middleware.BeforeNavigateToUrlMiddleware
import com.tezov.tuucho.shared.sample.middleware.ExceptionNavigateToUrlMiddleware

object MiddlewareModule {

    fun invoke() = module(ModuleGroupDomain.Middleware) {

        factory<ExceptionNavigateToUrlMiddleware> {
            ExceptionNavigateToUrlMiddleware()
        } bindOrdered NavigationMiddleware.ToUrl::class

        factory<BeforeNavigateToUrlMiddleware> {
            BeforeNavigateToUrlMiddleware(
                useCaseExecutor = get(),
                serverHealthCheck = get(),
                refreshMaterialCache = get(),
                getValueOrNullFromStore = get(),
            )
        } bindOrdered NavigationMiddleware.ToUrl::class

    }
}
