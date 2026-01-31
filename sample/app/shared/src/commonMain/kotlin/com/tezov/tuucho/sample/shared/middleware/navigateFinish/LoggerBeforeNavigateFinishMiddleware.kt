package com.tezov.tuucho.sample.shared.middleware.navigateFinish

import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol.Next.Companion.invoke
import com.tezov.tuucho.core.domain.tool.protocol.SystemInformationProtocol
import com.tezov.tuucho.sample.shared._system.Logger
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.FlowCollector

class LoggerBeforeNavigateFinishMiddleware(
    private val logger: Logger,
    private val systemInformation: SystemInformationProtocol
) : NavigationMiddleware.Finish {

    override suspend fun ProducerScope<Unit>.process(
        context: Unit,
        next: MiddlewareProtocol.Next<Unit, Unit>?,
    ) {
        logger.debug("THREAD") { systemInformation.currentThreadName() }
        logger.debug("NAVIGATION") { "finish" }
        next.invoke(context)
    }
}
