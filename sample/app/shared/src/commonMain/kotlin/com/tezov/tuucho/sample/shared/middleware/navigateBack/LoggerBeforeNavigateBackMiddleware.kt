package com.tezov.tuucho.sample.shared.middleware.navigateBack

import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.sample.shared._system.Logger

class LoggerBeforeNavigateBackMiddleware(
    private val logger: Logger
) : NavigationMiddleware.Back {

    override suspend fun process(
        context: NavigationMiddleware.Back.Context,
        next: MiddlewareProtocol.Next<NavigationMiddleware.Back.Context>?,
    ) {
        logger.thread()
        logger.debug("NAVIGATION") { "back: ${context.currentUrl} -> ${context.nextUrl}" }
        next?.invoke(context)
    }
}
