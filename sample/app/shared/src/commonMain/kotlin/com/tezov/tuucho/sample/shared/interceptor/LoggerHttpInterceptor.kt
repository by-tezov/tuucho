package com.tezov.tuucho.sample.shared.interceptor

import com.tezov.tuucho.core.data.repository.network.HttpInterceptor
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase.Output
import com.tezov.tuucho.core.domain.tool.protocol.SystemInformationProtocol
import com.tezov.tuucho.sample.shared._system.Logger
import io.ktor.client.request.HttpResponseData
import kotlinx.coroutines.flow.FlowCollector

class LoggerHttpInterceptor(
    private val logger: Logger,
    private val systemInformation: SystemInformationProtocol
) : HttpInterceptor {

    override suspend fun FlowCollector<HttpResponseData>.process(
        context: HttpInterceptor.Context,
        next: MiddlewareProtocol.Next<HttpInterceptor.Context, HttpResponseData>?
    ) {
        with(context.builder) {
            logger.debug("THREAD") { systemInformation.currentThreadName() }
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
            next?.invoke(context).also { response ->
                logger.debug("NETWORK:response") {
                    buildString {
                        //appendLine("${response?.statusCode}")
                    }
                }
            }
        }
    }
}
