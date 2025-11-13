package com.tezov.tuucho.shared.sample.middleware.updateView

import com.tezov.tuucho.core.domain.business.middleware.UpdateViewMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.shared.sample._system.Logger

class LoggerUpdateViewMiddleware(
    private val logger: Logger
) : UpdateViewMiddleware {

    override suspend fun process(
        context: UpdateViewMiddleware.Context,
        next: MiddlewareProtocol.Next<UpdateViewMiddleware.Context, Unit>,
    ) {
        with(logger) {
            debug("VIEW UPDATE") {
                buildString {
                    appendLine(context.input.route.value)
                    appendLine("-- input --")
                    appendLine(context.input.jsonObject.toString())
                }
            }
            next.invoke(context)
        }
    }
}
