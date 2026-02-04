package com.tezov.tuucho.sample.shared.middleware.navigateToUrl

import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.sample.shared._system.Logger

class LoggerBeforeNavigateToUrlMiddleware(
    private val logger: Logger
) : NavigationMiddleware.ToUrl {

    override suspend fun process(
        context: NavigationMiddleware.ToUrl.Context,
        next: MiddlewareProtocol.Next<NavigationMiddleware.ToUrl.Context>?,
    ) {
        logger.thread()
        logger.debug("NAVIGATION") { "forward: ${context.currentUrl} -> ${context.input.url}" }
        next?.invoke(context)
    }
}
