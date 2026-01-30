package com.tezov.tuucho.sample.shared.middleware.navigateToUrl

import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol.Next.Companion.invoke
import com.tezov.tuucho.core.domain.tool.protocol.SystemInformationProtocol
import com.tezov.tuucho.sample.shared._system.Logger
import kotlinx.coroutines.flow.FlowCollector

class LoggerBeforeNavigateToUrlMiddleware(
    private val logger: Logger,
    private val systemInformation: SystemInformationProtocol
) : NavigationMiddleware.ToUrl {

    override suspend fun FlowCollector<Unit>.process(
        context: NavigationMiddleware.ToUrl.Context,
        next: MiddlewareProtocol.Next<NavigationMiddleware.ToUrl.Context, Unit>?,
    ) {
        logger.debug("THREAD") { systemInformation.currentThreadName() }
        logger.debug("NAVIGATION") { "${context.currentUrl} -> ${context.input.url}" }
        next.invoke(context)
    }
}
