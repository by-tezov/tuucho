package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateToUrlUseCase
import com.tezov.tuucho.shared.sample.middleware.BeforeNavigateToUrlMiddleware
import com.tezov.tuucho.shared.sample.middleware.ExceptionNavigateToUrlMiddleware
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.bind

object MiddlewareModule {

    fun invoke(): ModuleDeclaration = {

        factory<ExceptionNavigateToUrlMiddleware> {
            ExceptionNavigateToUrlMiddleware()
        } bind NavigationMiddleware.ToUrl::class

        factory<BeforeNavigateToUrlMiddleware> {
            BeforeNavigateToUrlMiddleware(
                useCaseExecutor = get(),
                serverHealthCheck = get(),
                refreshMaterialCache = get(),
                getValueOrNullFromStore = get(),
            )
        } bind NavigationMiddleware.ToUrl::class

    }
}