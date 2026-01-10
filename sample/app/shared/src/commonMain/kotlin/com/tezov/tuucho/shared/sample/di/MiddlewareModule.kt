package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.domain.business._system.koin.BindOrdered.bindOrdered
import com.tezov.tuucho.core.domain.business.di.ModuleGroupDomain
import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.middleware.SendDataMiddleware
import com.tezov.tuucho.core.domain.business.middleware.UpdateViewMiddleware
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.module
import com.tezov.tuucho.core.domain.business.di.Koin
import com.tezov.tuucho.shared.sample.middleware.beforeNavigateBack.LoggerBeforeNavigateBackMiddleware
import com.tezov.tuucho.shared.sample.middleware.beforeNavigateToUrl.BeforeNavigateToUrlMiddleware
import com.tezov.tuucho.shared.sample.middleware.beforeNavigateToUrl.CatcherBeforeNavigateToUrlMiddleware
import com.tezov.tuucho.shared.sample.middleware.beforeNavigateToUrl.LoggerBeforeNavigateToUrlMiddleware
import com.tezov.tuucho.shared.sample.middleware.beforeNavigateToUrl.OnShadowerException
import com.tezov.tuucho.shared.sample.middleware.sendData.LoggerSendDataMiddleware
import com.tezov.tuucho.shared.sample.middleware.updateView.LoggerUpdateViewMiddleware
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

object MiddlewareModule {

    fun invoke() = module(ModuleGroupDomain.Middleware) {
        beforeNavigateToUrl()
        beforeNavigateBack()
        sendData()
        updateView()
    }

    private fun Module.beforeNavigateToUrl() {
        factoryOf(::CatcherBeforeNavigateToUrlMiddleware) bindOrdered NavigationMiddleware.ToUrl::class

        factory<BeforeNavigateToUrlMiddleware> {
            BeforeNavigateToUrlMiddleware(
                useCaseExecutor = get(),
                serverHealthCheck = get(),
                refreshMaterialCache = get(),
                getValueOrNullFromStore = get(),
                onShadowerException = OnShadowerException(),
            )
        } bindOrdered NavigationMiddleware.ToUrl::class

        factoryOf(::LoggerBeforeNavigateToUrlMiddleware) bindOrdered NavigationMiddleware.ToUrl::class
    }

    private fun Module.beforeNavigateBack() {
        factoryOf(::LoggerBeforeNavigateBackMiddleware) bindOrdered NavigationMiddleware.Back::class
    }

    private fun Module.sendData() {
        factoryOf(::LoggerSendDataMiddleware) bindOrdered SendDataMiddleware::class
    }

    private fun Module.updateView() {
        factoryOf(::LoggerUpdateViewMiddleware) bindOrdered UpdateViewMiddleware::class
    }
}
