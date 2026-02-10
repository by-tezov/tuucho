package com.tezov.tuucho.sample.shared.di

import com.tezov.tuucho.core.domain.business._system.koin.BindOrdered.bindOrdered
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.di.ModuleContextDomain
import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.middleware.RetrieveImageMiddleware
import com.tezov.tuucho.core.domain.business.middleware.SendDataMiddleware
import com.tezov.tuucho.core.domain.business.middleware.UpdateViewMiddleware
import com.tezov.tuucho.sample.shared.middleware.image.CatcherRetrieveImageMiddleware
import com.tezov.tuucho.sample.shared.middleware.image.LoggerRetrieveImageMiddleware
import com.tezov.tuucho.sample.shared.middleware.navigateBack.LoggerBeforeNavigateBackMiddleware
import com.tezov.tuucho.sample.shared.middleware.navigateFinish.LoggerBeforeNavigateFinishMiddleware
import com.tezov.tuucho.sample.shared.middleware.navigateFinish.NavigateFinishMiddleware
import com.tezov.tuucho.sample.shared.middleware.navigateFinish.NavigationFinishPublisher
import com.tezov.tuucho.sample.shared.middleware.navigateToUrl.BeforeNavigateToUrlMiddleware
import com.tezov.tuucho.sample.shared.middleware.navigateToUrl.CatcherBeforeNavigateToUrlMiddleware
import com.tezov.tuucho.sample.shared.middleware.navigateToUrl.LoggerBeforeNavigateToUrlMiddleware
import com.tezov.tuucho.sample.shared.middleware.sendData.LoggerSendDataMiddleware
import com.tezov.tuucho.sample.shared.middleware.updateView.LoggerUpdateViewMiddleware
import org.koin.core.module.Module
import org.koin.plugin.module.dsl.factory
import org.koin.plugin.module.dsl.single

object MiddlewareModule {

    fun invoke() = module(ModuleContextDomain.Middleware) {
        image()
        navigateToUrl()
        navigateBack()
        navigateFinish()
        sendData()
        updateView()
    }


    private fun Module.image() {
        factory<CatcherRetrieveImageMiddleware>() bindOrdered RetrieveImageMiddleware::class
        factory<LoggerRetrieveImageMiddleware>() bindOrdered RetrieveImageMiddleware::class
    }

    private fun Module.navigateToUrl() {
        factory<CatcherBeforeNavigateToUrlMiddleware>() bindOrdered NavigationMiddleware.ToUrl::class

        factory<BeforeNavigateToUrlMiddleware> {
            BeforeNavigateToUrlMiddleware(
                useCaseExecutor = get(),
                serverHealthCheck = get(),
                refreshMaterialCache = get(),
                getValueOrNullFromStore = get(),
            )
        } bindOrdered NavigationMiddleware.ToUrl::class

        factory<LoggerBeforeNavigateToUrlMiddleware>() bindOrdered NavigationMiddleware.ToUrl::class
    }

    private fun Module.navigateBack() {
        factory<LoggerBeforeNavigateBackMiddleware>() bindOrdered NavigationMiddleware.Back::class
    }

    private fun Module.navigateFinish() {
        single<NavigationFinishPublisher>()
        factory<NavigateFinishMiddleware>() bindOrdered NavigationMiddleware.Finish::class
        factory<LoggerBeforeNavigateFinishMiddleware>() bindOrdered NavigationMiddleware.Finish::class
    }

    private fun Module.sendData() {
        factory<LoggerSendDataMiddleware>() bindOrdered SendDataMiddleware::class
    }

    private fun Module.updateView() {
        factory<LoggerUpdateViewMiddleware>() bindOrdered UpdateViewMiddleware::class
    }
}
