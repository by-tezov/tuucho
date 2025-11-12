package com.tezov.tuucho.shared.sample.middleware.beforeNavigateToUrl

import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.shared.sample._system.Logger

class LoggerBeforeNavigateToUrlMiddleware(
    private val logger: Logger
) : NavigationMiddleware.ToUrl {

    override suspend fun process(
        context: NavigationMiddleware.ToUrl.Context,
        next: MiddlewareProtocol.Next<NavigationMiddleware.ToUrl.Context, Unit>,
    ) {
        with(logger){
            println("${context.currentUrl} -> ${context.input.url}")
            next.invoke(context)
        }
    }
}
