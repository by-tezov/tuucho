package com.tezov.tuucho.sample.shared.interceptor

import com.tezov.tuucho.core.data.repository.network.HttpInterceptor
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn.Next.Companion.intercept
import com.tezov.tuucho.sample.shared._system.Logger
import io.ktor.client.request.HttpResponseData
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.firstOrNull

class LoggerHttpInterceptor(
    private val logger: Logger
) : HttpInterceptor {

    override suspend fun ProducerScope<HttpResponseData>.process(
        context: HttpInterceptor.Context,
        next: MiddlewareProtocolWithReturn.Next<HttpInterceptor.Context, HttpResponseData>?
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
            val output = next.intercept(context)?.firstOrNull()
            output?.let {
                logger.debug("NETWORK:response") { "${it.statusCode}" }
            }
            output?.let { send(it) }
        }
    }
}
