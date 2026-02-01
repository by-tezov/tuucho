package com.tezov.tuucho.sample.shared.middleware.navigateFinish

import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.tool.protocol.SystemInformationProtocol
import com.tezov.tuucho.sample.shared._system.Logger
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.FlowCollector

class LoggerBeforeNavigateFinishMiddleware(
    private val logger: Logger
) : NavigationMiddleware.Finish {

    override suspend fun process(
        context: Unit,
        next: MiddlewareProtocol.Next<Unit>?,
    ) {
        logger.thread()
        logger.debug("NAVIGATION") { "finish" }
        next?.invoke(context)
    }
}
