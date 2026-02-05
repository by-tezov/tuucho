package com.tezov.tuucho.sample.shared.middleware.updateView

import com.tezov.tuucho.core.domain.business.middleware.UpdateViewMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.sample.shared._system.Logger

class LoggerUpdateViewMiddleware(
    private val logger: Logger
) : UpdateViewMiddleware {

    override suspend fun process(
        context: UpdateViewMiddleware.Context,
        next: MiddlewareProtocol.Next<UpdateViewMiddleware.Context>?,
    ) {
        with(context.input) {
            logger.thread()
            logger.debug("VIEW UPDATE") {
                buildString {
                    appendLine(route)
                    appendLine("-- input --")
                    appendLine(jsonObjects.toString())
                }
            }
            next?.invoke(context)
        }
    }
}
