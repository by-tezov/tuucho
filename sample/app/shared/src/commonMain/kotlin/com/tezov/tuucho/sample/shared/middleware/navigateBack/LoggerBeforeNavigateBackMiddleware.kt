package com.tezov.tuucho.sample.shared.middleware.navigateBack

import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Output
import com.tezov.tuucho.core.domain.tool.protocol.SystemInformationProtocol
import com.tezov.tuucho.sample.shared._system.Logger
import kotlinx.coroutines.flow.FlowCollector

class LoggerBeforeNavigateBackMiddleware(
    private val logger: Logger,
    private val systemInformation: SystemInformationProtocol
) : NavigationMiddleware.Back {

    override suspend fun FlowCollector<Unit>.process(
        context: NavigationMiddleware.Back.Context,
        next: MiddlewareProtocol.Next<NavigationMiddleware.Back.Context, Unit>?,
    ) {
        logger.debug("THREAD") { systemInformation.currentThreadName() }
        logger.debug("NAVIGATION") { "${context.currentUrl} -> ${context.nextUrl}" }
        next?.invoke(context)
    }
}
