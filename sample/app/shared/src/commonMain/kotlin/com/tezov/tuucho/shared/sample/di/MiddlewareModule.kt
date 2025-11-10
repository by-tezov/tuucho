package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.domain.business.di.ModuleGroupDomain
import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.tool.di.ExtensionKoin.bindOrdered
import com.tezov.tuucho.shared.sample.middleware.BeforeNavigateToUrlMiddleware
import com.tezov.tuucho.shared.sample.middleware.ExceptionNavigateToUrlMiddleware
import org.koin.core.module.Module

object MiddlewareModule {

    fun invoke() = object : ModuleProtocol {

        override val group = ModuleGroupDomain.Middleware

        override fun Module.declaration() {

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
}
