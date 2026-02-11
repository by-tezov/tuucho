package com.tezov.tuucho.sample.shared.middleware.sendData

import com.tezov.tuucho.core.domain.business.interaction.middleware.SendDataMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn.Next.Companion.intercept
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.SendDataUseCase.Output
import com.tezov.tuucho.sample.shared._system.Logger
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.firstOrNull

class LoggerSendDataMiddleware(
    private val logger: Logger
) : SendDataMiddleware {

    override suspend fun ProducerScope<Output>.process(
        context: SendDataMiddleware.Context,
        next: MiddlewareProtocolWithReturn.Next<SendDataMiddleware.Context, Output>?,
    ) {
        val output = next?.intercept(context)?.firstOrNull()
        logger.thread()
        logger.debug("SEND DATA") {
            buildString {
                appendLine(context.input.url)
                appendLine("-- sent --")
                appendLine(context.input.jsonObject.toString())
                appendLine("-- received --")
                appendLine(output)
            }
        }
        output?.let { send(it) }
    }
}
