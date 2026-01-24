package com.tezov.tuucho.sample.shared.interceptor

import com.tezov.tuucho.core.data.repository.assets.AssetSourceProtocol
import com.tezov.tuucho.core.data.repository.di.NetworkModule
import com.tezov.tuucho.core.data.repository.network.HttpInterceptor
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.sample.shared._system.Page
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.FlowCollector
import okio.buffer

class FailSafePageHttpInterceptor(
    private val config: NetworkModule.Config,
    private val assetSource: AssetSourceProtocol,
) : HttpInterceptor {
    override suspend fun FlowCollector<HttpResponseData>.process(
        context: HttpInterceptor.Context,
        next: MiddlewareProtocol.Next<HttpInterceptor.Context, HttpResponseData>?
    ) {
        with(context.builder) {
            val route = url.toString()
                .removePrefix("${config.baseUrl}/")
                .removePrefix("${config.version}/")
                .removePrefix(config.resourceEndpoint)
            if (route != "/${Page.failSafe}") {
                next?.invoke(context)
                return
            }
            emit(
                HttpResponseData(
                    statusCode = HttpStatusCode.OK,
                    requestTime = GMTDate(),
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    version = HttpProtocolVersion.HTTP_1_1,
                    body = ByteReadChannel(failSafeResponse),
                    callContext = executionContext
                )
            )
        }
    }

    private val failSafeResponse: String
        get() = assetSource.readFile(
            path = "json/fail-safe-page-http-interceptor.json"
        ) { content ->
            content.source.buffer().readUtf8()
        }
}
