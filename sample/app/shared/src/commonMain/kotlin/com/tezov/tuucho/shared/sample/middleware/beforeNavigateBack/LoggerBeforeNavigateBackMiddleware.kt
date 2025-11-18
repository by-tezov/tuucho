package com.tezov.tuucho.shared.sample.middleware.beforeNavigateBack

import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.shared.sample._system.Logger

class LoggerBeforeNavigateBackMiddleware(
    private val logger: Logger
) : NavigationMiddleware.Back {

    override suspend fun process(
        context: NavigationMiddleware.Back.Context,
        next: MiddlewareProtocol.Next<NavigationMiddleware.Back.Context, Unit>,
    ) {
        logger.debug("NAVIGATION") { "${context.currentUrl} -> ${context.nextUrl}" }
        next.invoke(context)
    }
}
