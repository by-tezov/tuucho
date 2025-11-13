package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.domain.business.di.ModuleGroupDomain
import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.middleware.SendDataMiddleware
import com.tezov.tuucho.core.domain.business.middleware.UpdateViewMiddleware
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.tool.extension.ExtensionKoin.bindOrdered
import com.tezov.tuucho.shared.sample.middleware.beforeNavigateToUrl.BeforeNavigateToUrlMiddleware
import com.tezov.tuucho.shared.sample.middleware.beforeNavigateToUrl.CatcherBeforeNavigateToUrlMiddleware
import com.tezov.tuucho.shared.sample.middleware.beforeNavigateToUrl.LoggerBeforeNavigateToUrlMiddleware
import com.tezov.tuucho.shared.sample.middleware.sendData.LoggerSendDataMiddleware
import com.tezov.tuucho.shared.sample.middleware.updateView.LoggerUpdateViewMiddleware
import org.koin.core.module.Module

object MiddlewareModule {

    fun invoke() = module(ModuleGroupDomain.Middleware) {
        beforeNavigateToUrl()
        sendData()
        updateView()
    }

    private fun Module.beforeNavigateToUrl() {
        factory<CatcherBeforeNavigateToUrlMiddleware> {
            CatcherBeforeNavigateToUrlMiddleware()
        } bindOrdered NavigationMiddleware.ToUrl::class

        factory<BeforeNavigateToUrlMiddleware> {
            BeforeNavigateToUrlMiddleware(
                useCaseExecutor = get(),
                serverHealthCheck = get(),
                refreshMaterialCache = get(),
                getValueOrNullFromStore = get(),
            )
        } bindOrdered NavigationMiddleware.ToUrl::class

        factory<LoggerBeforeNavigateToUrlMiddleware> {
            LoggerBeforeNavigateToUrlMiddleware(
                logger = get()
            )
        } bindOrdered NavigationMiddleware.ToUrl::class
    }

    private fun Module.sendData() {
        factory<LoggerSendDataMiddleware> {
            LoggerSendDataMiddleware(
                logger = get()
            )
        } bindOrdered SendDataMiddleware::class
    }

    private fun Module.updateView() {
        factory<LoggerUpdateViewMiddleware> {
            LoggerUpdateViewMiddleware(
                logger = get()
            )
        } bindOrdered UpdateViewMiddleware::class
    }
}
