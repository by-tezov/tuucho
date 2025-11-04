package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.domain.business.di.ModuleGroupDomain
import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.shared.sample.middleware.BeforeNavigateToUrlMiddleware
import com.tezov.tuucho.shared.sample.middleware.ExceptionNavigateToUrlMiddleware
import org.koin.core.module.Module
import org.koin.dsl.bind

object MiddlewareModule {

    fun invoke() = object : ModuleProtocol {

        override val group = ModuleGroupDomain.Middleware

        override fun Module.declaration() {

            factory<BeforeNavigateToUrlMiddleware> {
                BeforeNavigateToUrlMiddleware(
                    useCaseExecutor = get(),
                    serverHealthCheck = get(),
                    refreshMaterialCache = get(),
                    getValueOrNullFromStore = get(),
                )
            } bind NavigationMiddleware.ToUrl::class

            factory<ExceptionNavigateToUrlMiddleware> {
                ExceptionNavigateToUrlMiddleware()
            } bind NavigationMiddleware.ToUrl::class

        }
    }
}
