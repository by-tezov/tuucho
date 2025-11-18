package com.tezov.tuucho.shared.sample.middleware.sendData

import com.tezov.tuucho.core.domain.business.middleware.SendDataMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.SendDataUseCase
import com.tezov.tuucho.shared.sample._system.Logger

class LoggerSendDataMiddleware(
    private val logger: Logger
) : SendDataMiddleware {

    override suspend fun process(
        context: SendDataMiddleware.Context,
        next: MiddlewareProtocol.Next<SendDataMiddleware.Context, SendDataUseCase.Output>,
    ) = with(context.input) {
        val output = next.invoke(context)
        logger.debug("SEND DATA") {
            buildString {
                appendLine(url)
                appendLine("-- input --")
                appendLine(jsonObject.toString())
                appendLine("-- output --")
                appendLine(output)
            }
        }
        output
    }
}
