package com.tezov.tuucho.sample.shared.middleware.navigateBack

import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol.Next.Companion.invoke
import com.tezov.tuucho.core.domain.tool.protocol.SystemInformationProtocol
import com.tezov.tuucho.sample.shared._system.Logger
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.FlowCollector

class LoggerBeforeNavigateBackMiddleware(
    private val logger: Logger,
    private val systemInformation: SystemInformationProtocol
) : NavigationMiddleware.Back {

    override suspend fun ProducerScope<Unit>.process(
        context: NavigationMiddleware.Back.Context,
        next: MiddlewareProtocol.Next<NavigationMiddleware.Back.Context, Unit>?,
    ) {
        logger.debug("THREAD") { systemInformation.currentThreadName() }
        logger.debug("NAVIGATION") { "back: ${context.currentUrl} -> ${context.nextUrl}" }
        next.invoke(context)
    }
}
