package com.tezov.tuucho.sample.shared.middleware.http

import com.tezov.tuucho.core.data.repository.network.HttpExchangeMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn.Next.Companion.intercept
import com.tezov.tuucho.sample.shared._system.Logger
import io.ktor.client.request.HttpResponseData
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.firstOrNull

class LoggerHttpMiddleware(
    private val logger: Logger
) : HttpExchangeMiddleware {

    override suspend fun ProducerScope<HttpResponseData>.process(
        context: HttpExchangeMiddleware.Context,
        next: MiddlewareProtocolWithReturn.Next<HttpExchangeMiddleware.Context, HttpResponseData>?
    ) {
        with(context.requestBuilder) {
            logger.thread()
            logger.debug("NETWORK:request") {
                buildString {
                    appendLine("$method - $url")
//                    val entries = headers.entries()
//                    if (entries.isNotEmpty()) {
//                        appendLine("-- Headers --")
//                        entries.forEach { appendLine(it.toString()) }
//                        appendLine("-------------")
//                    }
                }
            }
            val output = next?.intercept(context)?.firstOrNull()
            output?.let {
                logger.debug("NETWORK:response") { "${it.statusCode}" }
            }
            output?.let { send(it) }
        }
    }
}
