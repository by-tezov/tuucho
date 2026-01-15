package com.tezov.tuucho.sample.shared.di

import com.tezov.tuucho.core.domain.business._system.koin.BindOrdered.bindOrdered
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.di.ModuleContextDomain
import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.middleware.SendDataMiddleware
import com.tezov.tuucho.core.domain.business.middleware.UpdateViewMiddleware
import com.tezov.tuucho.sample.shared.middleware.navigateBack.LoggerBeforeNavigateBackMiddleware
import com.tezov.tuucho.sample.shared.middleware.navigateFinish.NavigateFinishMiddleware
import com.tezov.tuucho.sample.shared.middleware.navigateFinish.NavigationFinishPublisher
import com.tezov.tuucho.sample.shared.middleware.navigateToUrl.BeforeNavigateToUrlMiddleware
import com.tezov.tuucho.sample.shared.middleware.navigateToUrl.CatcherBeforeNavigateToUrlMiddleware
import com.tezov.tuucho.sample.shared.middleware.navigateToUrl.LoggerBeforeNavigateToUrlMiddleware
import com.tezov.tuucho.sample.shared.middleware.navigateToUrl.OnShadowerException
import com.tezov.tuucho.sample.shared.middleware.sendData.LoggerSendDataMiddleware
import com.tezov.tuucho.sample.shared.middleware.updateView.LoggerUpdateViewMiddleware
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf

object MiddlewareModule {

    fun invoke() = module(ModuleContextDomain.Middleware) {
        navigateToUrl()
        navigateBack()
        navigateFinish()
        sendData()
        updateView()
    }

    private fun Module.navigateToUrl() {
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

    private fun Module.navigateBack() {
        factoryOf(::LoggerBeforeNavigateBackMiddleware) bindOrdered NavigationMiddleware.Back::class
    }

    private fun Module.navigateFinish() {
        singleOf(::NavigationFinishPublisher)
        factoryOf(::NavigateFinishMiddleware) bindOrdered NavigationMiddleware.Finish::class
    }

    private fun Module.sendData() {
        factoryOf(::LoggerSendDataMiddleware) bindOrdered SendDataMiddleware::class
    }

    private fun Module.updateView() {
        factoryOf(::LoggerUpdateViewMiddleware) bindOrdered UpdateViewMiddleware::class
    }
}
