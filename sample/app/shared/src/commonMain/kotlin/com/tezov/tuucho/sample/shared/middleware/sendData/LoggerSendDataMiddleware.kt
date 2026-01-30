package com.tezov.tuucho.sample.shared.middleware.sendData

import com.tezov.tuucho.core.domain.business.middleware.SendDataMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol.Next.Companion.intercept
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.SendDataUseCase.Output
import com.tezov.tuucho.core.domain.tool.protocol.SystemInformationProtocol
import com.tezov.tuucho.sample.shared._system.Logger
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.firstOrNull

class LoggerSendDataMiddleware(
    private val logger: Logger,
    private val systemInformation: SystemInformationProtocol
) : SendDataMiddleware {

    override suspend fun FlowCollector<Output>.process(
        context: SendDataMiddleware.Context,
        next: MiddlewareProtocol.Next<SendDataMiddleware.Context, Output>?,
    ) {
        val output = next.intercept(context)?.firstOrNull()
        logger.debug("THREAD") { systemInformation.currentThreadName() }
        logger.debug("SEND DATA") {
            buildString {
                appendLine(context.input.url)
                appendLine("-- sent --")
                appendLine(context.input.jsonObject.toString())
                appendLine("-- received --")
                appendLine(output)
            }
        }
        output?.let { emit(it) }
    }
}
