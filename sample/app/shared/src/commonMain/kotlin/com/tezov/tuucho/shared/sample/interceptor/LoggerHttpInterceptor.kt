package com.tezov.tuucho.shared.sample.interceptor

import com.tezov.tuucho.core.data.repository.network.HttpInterceptor
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.shared.sample._system.Logger
import io.ktor.client.request.HttpResponseData

class LoggerHttpInterceptor(
    private val logger: Logger
) : HttpInterceptor {

    override suspend fun process(
        context: HttpInterceptor.Context,
        next: MiddlewareProtocol.Next<HttpInterceptor.Context, HttpResponseData>
    ): HttpResponseData {
        with(logger) {
            debug("NETWORK:request") {
                buildString {
                    appendLine("${context.builder.method} - ${context.builder.url}")
//                    val entries = context.builder.headers.entries()
//                    if (entries.isNotEmpty()) {
//                        appendLine("-- Headers --")
//                        entries.forEach { appendLine(it.toString()) }
//                        appendLine("-------------")
//                    }
                }
            }
            val response = next.invoke(context)
            debug("NETWORK:response") {
                buildString {
                    appendLine("${response.statusCode}")
                }
            }
            return response
        }
    }


}
